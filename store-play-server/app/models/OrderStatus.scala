package models

import play.api.libs.json.{Json, OFormat}

case class OrderStatus(id: Long, status: String, order_id: Long)

object OrderStatus {
  implicit val imageFormat: OFormat[Image] = Json.format[Image]
}