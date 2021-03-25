package com.example.route

import akka.actor.typed.{ActorRef, ActorSystem}
import akka.actor.typed.scaladsl.AskPattern._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.util.Timeout
import com.example.UserRegistry._
import com.example.domain.{CatalogRegistrationForm, ConsulService, ConsulServices}
import com.example.{ConsulDiscovery, ConsulRegistry, JsonFormats, User, UserRegistry, Users}

import scala.concurrent.Future

class ConsulRoute(consulRegistry:ConsulRegistry, consulDiscovery:ConsulDiscovery)(implicit val system: ActorSystem[_]) {

  import JsonFormats._
  import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._

  private implicit val timeout = Timeout.create(system.settings.config.getDuration("my-app.routes.ask-timeout"))
   implicit val ec = system.executionContext

  def getServices(): Future[ConsulServices] = Future.successful(ConsulServices(Seq.empty))

  def getService(name: String): Future[ConsulService] = {
    consulDiscovery.getService(name).map(ConsulService)
  }

  def createService(form: CatalogRegistrationForm): Future[String] = {
    form.service match {
      case Some(s) =>
        consulRegistry.registerService(form)
        Future.successful("Done")
      case None => Future.successful("Service name is none")
    }
  }

  def deleteUser(name: String): Future[String] = Future.successful("Done")


  val consulRoutes: Route =
  pathPrefix("consul") {
    concat(
      //#users-get-delete
      pathEnd {
        concat(
          get {
            complete(getServices())
          },
          post {
            entity(as[CatalogRegistrationForm]) { cs =>
              onSuccess(createService(cs)) { performed =>
                complete((StatusCodes.Created, performed))
              }
            }
          })
      },
      path(Segment) { name =>
        concat(
          get {
            //#retrieve-user-info
            rejectEmptyResponse {
              onSuccess(getService(name)) { response =>
                complete(response)
              }
            }
            //#retrieve-user-info
          },
          delete {
            //#users-delete-logic
            onSuccess(deleteUser(name)) { performed =>
              complete((StatusCodes.OK, performed))
            }
          })
      })
  }
}