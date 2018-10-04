package htimur.services

import htimur.configs.MongoConstants
import htimur.models.Shard
import org.mongodb.scala.{Document, MongoClient}

import scala.concurrent.Await
import scala.concurrent.duration._

/**
  * @author Timur Khamrakulov <timur.khamrakulov@gmail.com>.
  */
trait ShardService {
  def getShards: Seq[Shard]
}

object ShardService {

  def apply(client: MongoClient) = new ShardServiceImpl(client)

  final class ShardServiceImpl(client: MongoClient) extends ShardService {

    private val collection = client
      .getDatabase(MongoConstants.CONFIG_DATABASE)
      .getCollection(MongoConstants.SHARDS_COLLECTION)

    override def getShards: Seq[Shard] = {
      val shards = collection.find().map(parseShardInformation).toFuture()
      Await.result(shards, 10.seconds)
    }

    private def parseShardInformation(item: Document): Shard = {
      val document          = item.toBsonDocument
      val shardId           = document.getString(MongoConstants.SHARDS_ID).getValue
      val serversDefinition = document.getString(MongoConstants.SHARDS_HOST).getValue
      val servers =
        if (serversDefinition.contains("/"))
          serversDefinition.substring(serversDefinition.indexOf('/') + 1)
        else serversDefinition
      Shard(shardId, "mongodb://" + servers)
    }
  }

}
