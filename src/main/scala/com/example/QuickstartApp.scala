package com.example

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.scaladsl.adapter._
import akka.cluster.typed.Cluster
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.management.cluster.bootstrap.ClusterBootstrap
import akka.management.scaladsl.AkkaManagement
import akka.{actor => classic}
import com.example.route.{TopLevelRoute, UserRoutes}
import com.pszymczyk.consul.{ConsulProcess, ConsulStarterBuilder}
import com.typesafe.config.ConfigFactory

import scala.concurrent.Future
import scala.io.StdIn
import scala.util.{Failure, Success}

//#main-class
object QuickstartApp {

  case class NodePort(nodeId : Int, httpPort: Int,loopBackHost: String )

  //#start-http-server
  private def startHttpServer(routes: Route,httpPort: Int)(implicit system: ActorSystem[_]): Future[Http.ServerBinding]  = {
    // Akka HTTP still needs a classic ActorSystem to start
    import system.executionContext

    val futureBinding: Future[Http.ServerBinding] = Http().newServerAt("localhost",httpPort).bind(routes)
    futureBinding.onComplete {
      case Success(binding) =>
        val address = binding.localAddress
        system.log.info("Server online at http://{}:{}/", address.getHostString, address.getPort)
      case Failure(ex) =>
        system.log.error("Failed to bind HTTP endpoint, terminating system", ex)
        system.terminate()
    }
    futureBinding
  }
  def main(args: Array[String]): Unit = {

    val nodeArg = if (args.isEmpty) 0 else args(0).toInt
    val nodePort = NodePort(0+nodeArg,9000+nodeArg,s"127.0.0.${nodeArg+1}")

    val httpPort =nodePort.httpPort

    val regularConfig = ConfigFactory.load()

    val customConf = ConfigFactory.parseString(s"""
              akka.remote.artery.canonical.hostname = ${nodePort.loopBackHost}
              akka.management.http.hostname = ${nodePort.loopBackHost}
      """).withFallback(regularConfig)
    val complete = ConfigFactory.load(customConf)

    println(s"starting embedded consul server and registering service  ")
    val consul: ConsulProcess = ConsulStarterBuilder.consulStarter().withHttpPort(8500).build().start()
    val consulRegistry = new ConsulRegistry(consul)

    val rootBehavior = Behaviors.setup[Nothing] { context =>
      val userRegistryActor = context.spawn(UserRegistry(), "UserRegistryActor")
      context.watch(userRegistryActor)
      val consulDiscovery = new ConsulDiscovery()(context.system)


      val routes = new TopLevelRoute(userRegistryActor,consulRegistry,consulDiscovery)(context.system)
      startHttpServer(routes.route,httpPort)(context.system)

      Behaviors.empty
    }



    val system = ActorSystem[Nothing](rootBehavior, "cluster-HelloAkka",complete)

    implicit val classicSystem: classic.ActorSystem = system.toClassic
    implicit val ec = system.executionContext
    AkkaManagement.get(classicSystem).start()
    ClusterBootstrap.get(classicSystem).start()

    val r = classicSystem.settings.config.getAnyRef("akka.remote.artery.canonical.hostname")
    val v = classicSystem.settings.config.getAnyRef("akka.management.http.hostname")
    system.log.info(s"akka.remote.artery.canonical.hostname ${r}")
    system.log.info(s"akka.management.http.hostname ${v}")
    StdIn.readLine() // let it run until user presses return
    consul.close()
    system.terminate()
    //#server-bootstrapping
  }
}
