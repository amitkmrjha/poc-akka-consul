package com.example.domain

case class ConsulService(info: String)
case class ConsulServices (services: Seq[ConsulService])
case class CatalogRegistrationForm(
                                  id:  Option[String] = None ,
                                  datacenter : Option[String] = None,
                                  node: String,
                                  address:String,
                                  //nodeMeta: Map[String,String] = Map.empty,
                                  taggedAddresses: Option[String] = None,
                                  service: Option[String] = None,
                                  check: Option[String] = None,
                                  writeRequest: Option[String] = None,
                                  skipNodeUpdate: Option[String] = None
                                  )

