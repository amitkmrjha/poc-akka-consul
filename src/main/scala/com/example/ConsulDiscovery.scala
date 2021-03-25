package com.example

import akka.actor.typed.ActorSystem
import akka.discovery.ServiceDiscovery.Resolved
import akka.discovery.consul.ConsulServiceDiscovery
import akka.actor.typed.scaladsl.adapter._
import akka.discovery.{Discovery, ServiceDiscovery}

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}

class ConsulDiscovery()(implicit val system: ActorSystem[_]) {

  implicit val classicSystem = system.toClassic

  implicit val ec: ExecutionContext = system.executionContext

  private val lookupService = new ConsulServiceDiscovery(classicSystem)
  //private val lookupService: ServiceDiscovery = Discovery.get(system).loadServiceDiscovery("akka-consul")

  def getService(consulServiceName:String ) = lookupService.lookup(consulServiceName, 10.seconds).map{
    case x:Resolved =>
      println(s"Consul Service ${consulServiceName} retrieved ${x.toString()}")
      x.toString()
    case _ => "Not found"
  }
}
