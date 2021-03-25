package com.example.route

import akka.actor.typed.{ActorRef, ActorSystem}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.coding.Coders
import akka.http.scaladsl.marshalling.ToResponseMarshaller
import akka.http.scaladsl.model.StatusCodes.MovedPermanently
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.unmarshalling.FromRequestUnmarshaller
import akka.pattern.ask
import akka.util.Timeout
import com.example.{ConsulDiscovery, ConsulRegistry}


class TopLevelRoute(consulRegistry:ConsulRegistry,consulDiscovery:ConsulDiscovery)
                   (implicit system: ActorSystem[_]) {
  lazy val  route: Route =
    concat(
      new ConsulRoute(consulRegistry,consulDiscovery).consulRoutes
    )
}
