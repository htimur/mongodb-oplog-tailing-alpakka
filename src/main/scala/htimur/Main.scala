package htimur

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Merge, RestartSource, Source}
import com.typesafe.config.ConfigFactory
import htimur.configs.MongoConfig
import htimur.services.{OplogService, ShardService}
import org.mongodb.scala.MongoClient
import org.mongodb.scala.bson.Document

import scala.concurrent.duration._

object Main extends App {

  val config      = ConfigFactory.load()
  val mongoConfig = MongoConfig(config.getConfig("mongodb-sharded-cluster"))

  val mongo        = MongoClient(mongoConfig.uri)
  val shardService = ShardService(mongo)
  val oplog        = OplogService()

  implicit val system: ActorSystem             = ActorSystem("MongoOplogTailer")
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  val shardOplogSources: Seq[Source[Document, NotUsed]] = shardService.getShards.map { shard =>
    RestartSource.withBackoff(
      minBackoff = 3.seconds,
      maxBackoff = 30.seconds,
      randomFactor = 0.2, // adds 20% "noise" to vary the intervals slightly
      maxRestarts = 20 // limits the amount of restarts to 20
    ) { () =>
      oplog.source(MongoClient(shard.uri))
    }
  }

  val allShards: Source[Document, NotUsed] = shardOplogSources.foldLeft(Source.empty[Document]) {
    (prev, current) =>
      Source.combine(prev, current)(Merge(_))
  }

  allShards.map(models.documentToOplogEntry).runForeach(println)
}
