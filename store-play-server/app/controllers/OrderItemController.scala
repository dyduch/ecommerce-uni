package controllers

import com.google.inject.Inject
import com.google.inject.Singleton
import models.{Product, ProductRepository, OrderItem, OrderItemRepository, Order, OrderRepository}
import play.api.data.Form
import play.api.data.Forms.{longNumber, mapping, nonEmptyText, number}
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.util.{Failure, Success};

@Singleton
class OrderItemController @Inject()(orderItemsRepo: OrderItemRepository, orderRepo: OrderRepository, productRepo: ProductRepository, cc: MessagesControllerComponents)(implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {
  val orderItemForm: Form[CreateOrderItemForm] = Form {
    mapping(
      "size" -> number,
      "quantity" -> number,
      "order" -> longNumber,
      "product" -> longNumber,
    )(CreateOrderItemForm.apply)(CreateOrderItemForm.unapply)
  }

  val updateOrderItemForm: Form[UpdateOrderItemForm] = Form {
    mapping(
      "id" -> longNumber,
      "size" -> number,
      "quantity" -> number,
      "order" -> longNumber,
      "product" -> longNumber,
    )(UpdateOrderItemForm.apply)(UpdateOrderItemForm.unapply)
  }


  def getAll: Action[AnyContent] = Action.async { implicit request =>
    val orderItems = orderItemsRepo.list()
    orderItems.map(ps => Ok(views.html.orderitems(ps)))
  }

  def get(id: Long): Action[AnyContent] = Action.async { implicit request =>
    val orderItem = orderItemsRepo.getByIdOption(id)
    orderItem.map {
      case Some(p) => Ok(views.html.orderitem(p))
      case None => Redirect(routes.OrderItemController.getAll())
    }
  }

  def delete(id: Long): Action[AnyContent] = Action {
    orderItemsRepo.delete(id)
    Redirect("/orderItems")
  }

  def update(id: Long): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    var order: Seq[Order] = Seq[Order]()
    var product: Seq[Product] = Seq[Product]()
    val orders: Unit = orderRepo.list().onComplete {
      case Success(usr) => order = usr
      case Failure(_) => print("fail")
    }

    val productes: Unit = productRepo.list().onComplete {
      case Success(a) => product = a
      case Failure(_) => print("fail")
    }

    val orderItem = orderItemsRepo.getById(id)
    orderItem.map(orderItem => {
      val prodForm = updateOrderItemForm.fill(UpdateOrderItemForm(orderItem.id, orderItem.size, orderItem.quantity, orderItem.order_id, orderItem.product_id))
      Ok(views.html.orderitemupdate(prodForm, order, product))
    })
  }

  def updateHandle(): Action[AnyContent] = Action.async { implicit request =>
    var order: Seq[Order] = Seq[Order]()
    var product: Seq[Product] = Seq[Product]()
    val orders: Unit = orderRepo.list().onComplete {
      case Success(usr) => order = usr
      case Failure(_) => print("fail")
    }

    val productes: Unit = productRepo.list().onComplete {
      case Success(a) => product = a
      case Failure(_) => print("fail")
    }

    updateOrderItemForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.orderitemupdate(errorForm, order, product))
        )
      },
      orderItem => {
        orderItemsRepo.update(orderItem.id, OrderItem(orderItem.id, orderItem.size, orderItem.quantity, orderItem.order_id, orderItem.product_id)).map { _ =>
          Redirect(routes.OrderItemController.update(orderItem.id)).flashing("success" -> "orderItem updated")
        }
      }
    )

  }

  def add(): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    val orders = orderRepo.list()
    val productes = Await.result(productRepo.list(), Duration.create(60, "seconds"))
    orders.map(usr => Ok(views.html.orderitemadd(orderItemForm, usr, productes)))
  }

  def addHandle(): Action[AnyContent] = Action.async { implicit request =>
    var order: Seq[Order] = Seq[Order]()
    var product: Seq[Product] = Seq[Product]()
    val orders: Unit = orderRepo.list().onComplete {
      case Success(usr) => order = usr
      case Failure(_) => print("fail")
    }

    val productes: Unit = productRepo.list().onComplete {
      case Success(a) => product = a
      case Failure(_) => print("fail")
    }


    orderItemForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.orderitemadd(errorForm, order, product))
        )
      },
      orderItem => {
        orderItemsRepo.create(orderItem.size, orderItem.quantity, orderItem.order_id, orderItem.product_id).map { _ =>
          Redirect(routes.OrderItemController.add()).flashing("success" -> "orderItem.created")
        }
      }
    )

  }

  def addJSON(size: Int, quantity: Int, order_id: Long, product_id: Long): Action[AnyContent] = Action.async { implicit request =>
    orderItemsRepo.create(size, quantity, order_id, product_id).map {
      res => Ok(Json.toJson(res))
    }
  }

  def getAllJSON: Action[AnyContent] = Action.async { implicit request =>
    val orderItems = orderItemsRepo.list()
    orderItems.map(orderItems => Ok(Json.toJson(orderItems)))
  }

  def getJSON(id: Long): Action[AnyContent] = Action.async { implicit request =>
    val orderItem = orderItemsRepo.getByIdOption(id)
    orderItem.map {
      case orderItem@Some(p) => Ok(Json.toJson(orderItem))
      case None => Redirect(routes.OrderItemController.getAllJSON)
    }
  }

  def getForOrderJSON(id: Long): Action[AnyContent] = Action.async { implicit request =>
    val orderItems = orderItemsRepo.getByOrder(id)
    orderItems.map(orderItems => Ok(Json.toJson(orderItems)))
  }

  def deleteJSON(id: Long): Action[AnyContent] = Action.async { implicit request =>
    orderItemsRepo.delete(id).map {
      _ => Ok(Json.toJson(id))
    }
  }

  def updateJSON(id: Long, size: Int, quantity: Int, order_id: Long, product_id: Long): Action[AnyContent] = Action.async { implicit request =>
    orderItemsRepo.update(id, new OrderItem(id, size, quantity, order_id, product_id)).map {
      _ => Ok(Json.toJson(id))
    }
  }

}

case class CreateOrderItemForm(size: Int, quantity: Int, order_id: Long, product_id: Long)

case class UpdateOrderItemForm(id: Long, size: Int, quantity: Int, order_id: Long, product_id: Long)