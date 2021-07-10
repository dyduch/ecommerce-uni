package models

import play.api.libs.json.{Json, OFormat}

case class ProductQuantity(id: Long, quantity: Int, size: Int, product: Long)

object ProductQuantity {
  implicit val productQuantityFormat: OFormat[ProductQuantity] = Json.format[ProductQuantity]
}