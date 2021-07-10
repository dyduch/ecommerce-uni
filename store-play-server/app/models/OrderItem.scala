package models

import play.api.libs.json.{Json, OFormat}

case class OrderItem(id: Long, size: Int, quantity: Int, order: Long, product: Long)

object OrderItem {
  implicit val SupplierFormat: OFormat[Supplier] = Json.format[Supplier]
}