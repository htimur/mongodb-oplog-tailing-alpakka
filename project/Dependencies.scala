import sbt._

object Dependencies {
  lazy val enumeratum   = "com.beachape"       %% "enumeratum"                  % "1.5.13"
  lazy val logback      = "ch.qos.logback"     % "logback-classic"              % "1.2.3"
  lazy val scalaTest    = "org.scalatest"      %% "scalatest"                   % "3.0.5"
  lazy val alpakkaMongo = "com.lightbend.akka" %% "akka-stream-alpakka-mongodb" % "1.0-M1"
}
