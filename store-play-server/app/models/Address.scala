package models

import play.api.libs.json.{Json, OFormat}

case class Address(id: Long, street: String, number: String, city: String, zipcode: String, country: String)

object Address {
  implicit val addressFormat: OFormat[Address] = Json.format[Address]
}