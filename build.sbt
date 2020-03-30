val akkaVersion = "2.5.26"
val akkaHttpVersion = "10.1.11"

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "oen",
      scalaVersion := "2.13.1",
      version      := "1.6"
    )),
    name := "httpviewer",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-actor" % akkaVersion,
      "com.typesafe.akka" %% "akka-stream" % akkaVersion,
      "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
      "ch.qos.logback" % "logback-classic" % "1.1.3"
    )
  ).enablePlugins(JavaAppPackaging)
