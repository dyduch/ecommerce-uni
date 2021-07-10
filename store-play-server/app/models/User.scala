package models

import play.api.libs.json.{Json, OFormat}

case class User(id: Long, name: String, password: String, admin: Boolean, address_id: Long)

object User {
  implicit val SupplierFormat: OFormat[Supplier] = Json.format[Supplier]
}