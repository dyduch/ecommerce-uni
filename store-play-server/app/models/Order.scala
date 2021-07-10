package models

import play.api.libs.json.{Json, OFormat}

case class Order(id: Long, date: String, total: Double, user_id: Long, address_id: Long)

object Order {
  implicit val SupplierFormat: OFormat[Supplier] = Json.format[Supplier]
}