package models

import play.api.libs.json.{Json, OFormat}

case class Supplier(id: Long, name: String, address: Long)

object Supplier {
  implicit val SupplierFormat: OFormat[Supplier] = Json.format[Supplier]
}