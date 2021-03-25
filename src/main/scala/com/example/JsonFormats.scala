package com.example

import com.example.UserRegistry.ActionPerformed
import com.example.domain.{CatalogRegistrationForm, ConsulService, ConsulServices}

//#json-formats
import spray.json.DefaultJsonProtocol

object JsonFormats  {
  // import the default encoders for primitive types (Int, String, Lists etc)
  import DefaultJsonProtocol._

  implicit val consulServiceJsonFormat = jsonFormat1(ConsulService)
  implicit val consulServicesJsonFormat = jsonFormat1(ConsulServices)
  implicit val catalogRegistrationFormJsonFormat = jsonFormat9(CatalogRegistrationForm)

  implicit val userJsonFormat = jsonFormat3(User)
  implicit val usersJsonFormat = jsonFormat1(Users)

  implicit val actionPerformedJsonFormat = jsonFormat1(ActionPerformed)
}
//#json-formats
