package oen.httpviewer

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory

import scala.concurrent.Future
import scala.util.{Failure, Success}

object Main extends App {
  val config = ConfigFactory.load()

  val host = config.getString("http.host")
  val port = config.getInt("http.port")

  implicit val system = ActorSystem("httpviewer", config)
  implicit val materializer = ActorMaterializer()

  val routes: Route = get {
      getFromBrowseableDirectory("./")
  }

  val bindingFuture: Future[Http.ServerBinding] = Http().bindAndHandle(routes, host, port = port)

  val log =  Logging(system.eventStream, "app-service")

  import scala.concurrent.ExecutionContext.Implicits.global
  bindingFuture.onComplete {
    case Success(serverBinding) =>
      log.info("Bound to {}", serverBinding.localAddress)
    case  Failure(t) =>
      log.error(t, "Failed to bind to {}:{}!", host, port)
      system.terminate()
  }
}
