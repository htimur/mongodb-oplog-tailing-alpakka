package htimur

import enumeratum.values._
import org.mongodb.scala.bson.collection.immutable.Document
import org.mongodb.scala.bson.{BsonDocument, BsonTimestamp}

import scala.collection.immutable.{IndexedSeq => iSeq}

/**
  * @author Timur Khamrakulov <timur.khamrakulov@gmail.com>.
  */
object models {

  sealed abstract class OplogOperation(val value: String) extends StringEnumEntry

  case object OplogOperation extends StringEnum[OplogOperation] {
    val values: iSeq[OplogOperation] = findValues
    val projections: Seq[String]     = values.map(_.value)

    case object Insert extends OplogOperation("i")

    case object Update extends OplogOperation("u")

    case object Delete extends OplogOperation("d")

  }

  case class OplogEntry(ns: String, oRo2: BsonDocument, ts: BsonTimestamp, op: OplogOperation)

  def documentToOplogEntry(doc: Document): OplogEntry = {
    val entry = doc.toBsonDocument
    val ns    = entry.getString("ns").getValue
    val ts    = entry.getTimestamp("ts")
    val oRo2 =
      if (doc.get("o").isDefined)
        entry.getDocument("o")
      else
        entry.getDocument("o2")
    val op = OplogOperation.withValue(
      entry
        .getString("op")
        .getValue
    )

    OplogEntry(ns, oRo2, ts, op)
  }

  case class Shard(name: String, uri: String)

}
