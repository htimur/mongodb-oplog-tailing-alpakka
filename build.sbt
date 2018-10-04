import Dependencies._

lazy val root = (project in file("."))
  .settings(
    inThisBuild(
      List(
        organization := "com.example",
        scalaVersion := "2.12.7",
        version := "0.1.0-SNAPSHOT"
      )),
    name := "Mongodb oplog tailing alpakka",
    libraryDependencies += alpakkaMongo,
    libraryDependencies += enumeratum,
    libraryDependencies += logback,
    libraryDependencies += scalaTest % Test
  )
  .enablePlugins(DockerPlugin)
  .settings(dockerSettings)

lazy val dockerSettings =
  Seq(
    packageName in Docker := "oplog_tailer",
    dockerBaseImage := "openjdk:8-slim",
    dockerUpdateLatest := true,
    publishArtifact := false,
    mainClass in Compile := Some("htimur.Main"),
    javaOptions in Universal ++= Seq(
      // -J params will be added as jvm parameters
      "-J-Xmx512m",
      "-J-Xms256m",
      "-J-server"
    ),
    publishTo := Some(Resolver.file("devnull", file("/dev/null")))
  )
