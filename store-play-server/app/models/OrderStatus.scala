package models

import play.api.libs.json.{Json, OFormat}

case class OrderStatus(id: Long, status: String, order_id: Long)

object OrderStatus {
  implicit val orderStatusFormat: OFormat[OrderStatus] = Json.format[OrderStatus]
}