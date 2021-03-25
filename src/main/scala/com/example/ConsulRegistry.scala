package com.example

import com.example.domain.CatalogRegistrationForm
import com.google.common.net.HostAndPort
import com.orbitz.consul.Consul
import com.orbitz.consul.model.catalog.ImmutableCatalogRegistration
import com.orbitz.consul.model.health.ImmutableService
import com.pszymczyk.consul.{ConsulProcess, ConsulStarterBuilder}

class ConsulRegistry(consul: ConsulProcess) {

  //private val consul: ConsulProcess = ConsulStarterBuilder.consulStarter().withHttpPort(8500).build().start()

  def registerService(form:CatalogRegistrationForm):Unit = {
    val serviceName = form.service.getOrElse("NA")
    val consulAgent =
      Consul.builder().withHostAndPort(HostAndPort.fromParts(consul.getAddress, consul.getHttpPort)).build()
    consulAgent
      .catalogClient()
      .register(
        ImmutableCatalogRegistration
          .builder()
          .service(
            ImmutableService
              .builder()
              .addTags(s"system:${serviceName}", s"akka-management-port:${form.port.getOrElse(0)}")
              .address(form.address)
              .id(form.id.getOrElse("NA"))
              .service(serviceName)
              .port(form.port.getOrElse(0))
              .build()
          )
          .node("testNode")
          .address("localhost")
          .build()
      )
  }
}
