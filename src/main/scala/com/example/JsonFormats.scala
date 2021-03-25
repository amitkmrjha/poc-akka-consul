package com.example

import com.example.domain.{CatalogRegistrationForm, ConsulService, ConsulServices}

//#json-formats
import spray.json.DefaultJsonProtocol

object JsonFormats  {
  // import the default encoders for primitive types (Int, String, Lists etc)
  import DefaultJsonProtocol._

  implicit val consulServiceJsonFormat = jsonFormat1(ConsulService)
  implicit val consulServicesJsonFormat = jsonFormat1(ConsulServices)
  implicit val catalogRegistrationFormJsonFormat = jsonFormat10(CatalogRegistrationForm)

}
//#json-formats
