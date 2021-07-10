package controllers

import com.google.inject.Inject
import com.google.inject.Singleton
import models.{Order, OrderRepository, OrderStatus, OrderStatusRepository}
import play.api.data.Form
import play.api.data.Forms.{longNumber, mapping, nonEmptyText, number}
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success};

@Singleton
class OrderStatusController @Inject()(orderStatusesRepo: OrderStatusRepository, orderRepo: OrderRepository, cc: MessagesControllerComponents)(implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {
  val orderStatusForm: Form[CreateOrderStatusForm] = Form {
    mapping(
      "status" -> nonEmptyText,
      "order" -> longNumber,
    )(CreateOrderStatusForm.apply)(CreateOrderStatusForm.unapply)
  }

  val updateForm: Form[UpdateOrderStatusForm] = Form {
    mapping(
      "id" -> longNumber,
      "status" -> nonEmptyText,
      "order" -> longNumber,
    )(UpdateOrderStatusForm.apply)(UpdateOrderStatusForm.unapply)
  }


  def getAll: Action[AnyContent] = Action.async { implicit request =>
    val orderStatuses = orderStatusesRepo.list()
    orderStatuses.map(ps => Ok(views.html.orderstatuses(ps)))
  }

  def get(id: Long): Action[AnyContent] = Action.async { implicit request =>
    val orderStatus = orderStatusesRepo.getByIdOption(id)
    orderStatus.map {
      case Some(p) => Ok(views.html.orderstatus(p))
      case None => Redirect(routes.OrderStatusController.getAll())
    }
  }


  def delete(id: Long): Action[AnyContent] = Action {
    orderStatusesRepo.delete(id)
    Redirect("/orderStatuses")
  }

  def update(id: Long): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    var prod: Seq[Order] = Seq[Order]()
    val orders: Unit = orderRepo.list().onComplete {
      case Success(p) => prod = p
      case Failure(_) => print("fail")
    }

    val orderStatus = orderStatusesRepo.getById(id)
    orderStatus.map(orderStatus => {
      val prodForm = updateForm.fill(UpdateOrderStatusForm(orderStatus.id, orderStatus.status, orderStatus.order_id))
      Ok(views.html.orderstatusupdate(prodForm, prod))
    })
  }

  def updateHandle(): Action[AnyContent] = Action.async { implicit request =>
    var prod: Seq[Order] = Seq[Order]()
    val orders: Unit = orderRepo.list().onComplete {
      case Success(p) => prod = p
      case Failure(_) => print("fail")
    }

    updateForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.orderstatusupdate(errorForm, prod))
        )
      },
      orderStatus => {
        orderStatusesRepo.update(orderStatus.id, OrderStatus(orderStatus.id, orderStatus.status, orderStatus.order_id)).map { _ =>
          Redirect(routes.OrderStatusController.update(orderStatus.id)).flashing("success" -> "orderStatus updated")
        }
      }
    )

  }

  def add(): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    val orders = orderRepo.list()
    orders.map(p => Ok(views.html.orderstatusadd(orderStatusForm, p)))
  }

  def addHandle(): Action[AnyContent] = Action.async { implicit request =>
    var prod: Seq[Order] = Seq[Order]()
    val orders: Unit = orderRepo.list().onComplete {
      case Success(p) => prod = p
      case Failure(_) => print("fail")
    }

    orderStatusForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.orderstatusadd(errorForm, prod))
        )
      },
      orderStatus => {
        orderStatusesRepo.create(orderStatus.status, orderStatus.order_id).map { _ =>
          Redirect(routes.OrderStatusController.add()).flashing("success" -> "orderStatus.created")
        }
      }
    )

  }

  def addJSON(status: String, order_id: Long): Action[AnyContent] = Action.async { implicit request =>
    orderStatusesRepo.create(status, order_id).map {
      res => Ok(Json.toJson(res))
    }
  }

  def getAllJSON: Action[AnyContent] = Action.async { implicit request =>
    val orderStatuses = orderStatusesRepo.list()
    orderStatuses.map(orderStatuses => Ok(Json.toJson(orderStatuses)))
  }

  def getJSON(id: Long): Action[AnyContent] = Action.async { implicit request =>
    val orderStatus = orderStatusesRepo.getByIdOption(id)
    orderStatus.map {
      case orderStatus@Some(p) => Ok(Json.toJson(orderStatus))
      case None => Redirect(routes.OrderStatusController.getAllJSON)
    }
  }

  def getAllOrderJSON(id: Long): Action[AnyContent] = Action.async { implicit request =>
    val orderStatus = orderStatusesRepo.getByOrder(id)
    orderStatus.map(orderStatuses => Ok(Json.toJson(orderStatuses)))
  }

  def deleteJSON(id: Long): Action[AnyContent] = Action.async { implicit request =>
    orderStatusesRepo.delete(id).map {
      _ => Ok(Json.toJson(id))
    }
  }

  def updateJSON(id: Long, status: String, order_id: Long): Action[AnyContent] = Action.async { implicit request =>
    orderStatusesRepo.update(id, new OrderStatus(id, status, order_id)).map {
      _ => Ok(Json.toJson(id))
    }
  }

}

case class CreateOrderStatusForm(status: String, order_id: Long)

case class UpdateOrderStatusForm(id: Long, status: String, order_id: Long)