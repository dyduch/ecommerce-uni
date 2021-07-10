package models

import play.api.libs.json.{Json, OFormat}

case class Order(id: Long, date: String, total: Double, user: Long, address: Long)

object Order {
  implicit val SupplierFormat: OFormat[Supplier] = Json.format[Supplier]
}