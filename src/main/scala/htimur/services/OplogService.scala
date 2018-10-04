package htimur.services

import akka.NotUsed
import akka.stream.alpakka.mongodb.scaladsl.MongoSource
import akka.stream.scaladsl.Source
import com.mongodb.CursorType
import htimur.configs.MongoConstants
import htimur.models.OplogOperation
import org.mongodb.scala.bson.collection.immutable.Document
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.{FindObservable, MongoClient}

trait OplogService {

  def source(client: MongoClient): Source[Document, NotUsed]

}

object OplogService {
  def apply() = new OplogServiceImpl

  class OplogServiceImpl extends OplogService {

    override def source(client: MongoClient): Source[Document, NotUsed] = {
      MongoSource(getOplogObservable(client))
    }

    private def getOplogObservable(client: MongoClient): FindObservable[Document] = {
      client
        .getDatabase(MongoConstants.LOCAL_DATABASE)
        .getCollection(MongoConstants.OPLOG_COLLECTION)
        .find(and(in(MongoConstants.OPLOG_OPERATION, OplogOperation.projections: _*),
                  exists(MongoConstants.OPLOG_FROM_MIGRATE, exists = false)))
        .cursorType(CursorType.TailableAwait)
        .noCursorTimeout(noCursorTimeout = true)
    }
  }

}
