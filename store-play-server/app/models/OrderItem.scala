package models

import play.api.libs.json.{Json, OFormat}

case class OrderItem(id: Long, size: Int, quantity: Int, order_id: Long, product_id: Long)

object OrderItem {
  implicit val OrderItemFormat: OFormat[OrderItem] = Json.format[OrderItem]
}