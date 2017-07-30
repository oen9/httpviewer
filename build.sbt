val akkaVersion = "2.5.3"
val akkaHttpVersion = "10.0.9"

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "oen",
      scalaVersion := "2.12.3",
      version      := "1.0"
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
