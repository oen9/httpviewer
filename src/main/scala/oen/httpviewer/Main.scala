package oen.httpviewer

import java.nio.file.{Files, Paths}
import java.util.Calendar

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.FileIO
import com.typesafe.config.ConfigFactory

import scala.concurrent.Future
import scala.util.{Failure, Success}

object Main extends App {
  val config = ConfigFactory.load()

  val host = config.getString("http.host")
  val port = config.getInt("http.port")

  implicit val system = ActorSystem("httpviewer", config)
  implicit val materializer = ActorMaterializer()

  val routes: Route =
    pathEndOrSingleSlash {
      getFromResource("index.html")
    } ~
    get {
      path("success") {
        getFromResource("success.html")
      } ~
      path("failed") {
        getFromResource("failed.html")
      } ~
      pathPrefix("list") {
        getFromBrowseableDirectory("./")
      }
    } ~
    post {
      path("upload") {
        withoutSizeLimit {
          fileUpload("file-to-upload") {
            case (metadata, byteSource) =>

              val currDateTime = Calendar.getInstance().getTime()
              val destDir = s"./uploaded/$currDateTime/"
              val destDirPaths = Paths.get(destDir)
              Files.createDirectories(destDirPaths)

              val destFile = s"$destDir/${metadata.fileName}"
              val destFilePath = Paths.get(destFile)
              val fileSink = FileIO.toPath(destFilePath)

              val uploadedFile = byteSource.runWith(fileSink)

              onSuccess(uploadedFile) { result =>
                result.wasSuccessful match {
                  case true => redirect("/success", StatusCodes.SeeOther)
                  case false => redirect("/failed", StatusCodes.SeeOther)
                }
              }
          }
        }
      }
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
