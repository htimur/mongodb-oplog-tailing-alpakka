import sbt._

object Dependencies {
  lazy val scalaTest    = "org.scalatest"      %% "scalatest"                   % "3.0.5"
  lazy val alpakkaMongo = "com.lightbend.akka" %% "akka-stream-alpakka-mongodb" % "1.0-M1"
}
