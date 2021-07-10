package controllers

import java.text.SimpleDateFormat
import java.util.Date

import com.google.inject.Inject
import com.google.inject.Singleton
import models.{Address, AddressRepository, Order, OrderRepository, User, UserRepository}
import play.api.data.Form
import play.api.data.Forms.{date, longNumber, mapping, nonEmptyText, number}
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.util.{Failure, Success};

@Singleton
class OrderController @Inject()(ordersRepo: OrderRepository, userRepo: UserRepository, addressRepo: AddressRepository, cc: MessagesControllerComponents)(implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {
  val orderForm: Form[CreateOrderForm] = Form {
    mapping(
      "date" -> date,
      "total" -> number,
      "user" -> longNumber,
      "address" -> longNumber,
    )(CreateOrderForm.apply)(CreateOrderForm.unapply)
  }

  val updateOrderForm: Form[UpdateOrderForm] = Form {
    mapping(
      "id" -> longNumber,
      "date" -> date,
      "total" -> number,
      "user" -> longNumber,
      "address" -> longNumber,
    )(UpdateOrderForm.apply)(UpdateOrderForm.unapply)
  }


  def getAll: Action[AnyContent] = Action.async { implicit request =>
    val orders = ordersRepo.list()
    orders.map(ps => Ok(views.html.orders(ps)))
  }

  def get(id: Long): Action[AnyContent] = Action.async { implicit request =>
    val order = ordersRepo.getByIdOption(id)
    order.map {
      case Some(p) => Ok(views.html.order(p))
      case None => Redirect(routes.OrderController.getAll())
    }
  }

  def delete(id: Long): Action[AnyContent] = Action {
    ordersRepo.delete(id)
    Redirect("/orders")
  }

  def update(id: Long): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    var user: Seq[User] = Seq[User]()
    var address: Seq[Address] = Seq[Address]()
    val users: Unit = userRepo.list().onComplete {
      case Success(usr) => user = usr
      case Failure(_) => print("fail")
    }

    val addresses: Unit = addressRepo.list().onComplete {
      case Success(a) => address = a
      case Failure(_) => print("fail")
    }

    val order = ordersRepo.getById(id)
    order.map(order => {
      val prodForm = updateOrderForm.fill(UpdateOrderForm(order.id, new SimpleDateFormat("yyyy-MM-dd").parse(order.date), order.total, order.user_id, order.address_id))
      Ok(views.html.orderupdate(prodForm, user, address))
    })
  }

  def updateHandle(): Action[AnyContent] = Action.async { implicit request =>
    var user: Seq[User] = Seq[User]()
    var address: Seq[Address] = Seq[Address]()
    val users: Unit = userRepo.list().onComplete {
      case Success(usr) => user = usr
      case Failure(_) => print("fail")
    }

    val addresses: Unit = addressRepo.list().onComplete {
      case Success(a) => address = a
      case Failure(_) => print("fail")
    }

    updateOrderForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.orderupdate(errorForm, user, address))
        )
      },
      order => {
        ordersRepo.update(order.id, Order(order.id, new SimpleDateFormat("yyyy-MM-dd").format(order.date), order.total, order.user_id, order.address_id)).map { _ =>
          Redirect(routes.OrderController.update(order.id)).flashing("success" -> "order updated")
        }
      }
    )

  }

  def add(): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    val users = userRepo.list()
    val addresses = Await.result(addressRepo.list(), Duration.create(60, "seconds"))
    users.map(usr => Ok(views.html.orderadd(orderForm, usr, addresses)))
  }

  def addHandle(): Action[AnyContent] = Action.async { implicit request =>
    var user: Seq[User] = Seq[User]()
    var address: Seq[Address] = Seq[Address]()
    val users: Unit = userRepo.list().onComplete {
      case Success(usr) => user = usr
      case Failure(_) => print("fail")
    }

    val addresses: Unit = addressRepo.list().onComplete {
      case Success(a) => address = a
      case Failure(_) => print("fail")
    }


    orderForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.orderadd(errorForm, user, address))
        )
      },
      order => {
        ordersRepo.create(new SimpleDateFormat("yyyy-MM-dd").format(order.date), order.total, order.user_id, order.address_id).map { _ =>
          Redirect(routes.OrderController.add()).flashing("success" -> "order.created")
        }
      }
    )

  }

  def addJSON(date: String, total: Int, user_id: Long, address_id: Long): Action[AnyContent] = Action.async { implicit request =>
    ordersRepo.create(date, total, user_id, address_id).map {
      res => Ok(Json.toJson(res))
    }
  }

  def getAllJSON: Action[AnyContent] = Action.async { implicit request =>
    val orders = ordersRepo.list()
    orders.map(orders => Ok(Json.toJson(orders)))
  }

  def getJSON(id: Long): Action[AnyContent] = Action.async { implicit request =>
    val order = ordersRepo.getByIdOption(id)
    order.map {
      case order@Some(p) => Ok(Json.toJson(order))
      case None => Redirect(routes.OrderController.getAllJSON)
    }
  }

  def getForUserJSON(id: Long): Action[AnyContent] = Action.async { implicit request =>
    val orders = ordersRepo.getByUser(id)
    orders.map(orders => Ok(Json.toJson(orders)))
  }

  def deleteJSON(id: Long): Action[AnyContent] = Action.async { implicit request =>
    ordersRepo.delete(id).map {
      _ => Ok(Json.toJson(id))
    }
  }

  def updateJSON(id: Long, date: String, total: Int, user_id: Long, address_id: Long): Action[AnyContent] = Action.async { implicit request =>
    ordersRepo.update(id, new Order(id, date, total, user_id, address_id)).map {
      _ => Ok(Json.toJson(id))
    }
  }

}

case class CreateOrderForm(date: Date, total: Int, user_id: Long, address_id: Long)

case class UpdateOrderForm(id: Long, date: Date, total: Int, user_id: Long, address_id: Long)