package controllers

import com.google.inject.Inject
import com.google.inject.Singleton
import models.{Product, ProductRepository, ProductQuantity, ProductQuantityRepository, Order, OrderRepository}
import play.api.data.Form
import play.api.data.Forms.{longNumber, mapping, nonEmptyText, number}
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.util.{Failure, Success};

@Singleton
class ProductQuantityController @Inject()(pqRepo: ProductQuantityRepository, productRepo: ProductRepository, cc: MessagesControllerComponents)(implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {
  val pqForm: Form[CreateProductQuantityForm] = Form {
    mapping(
      "size" -> number,
      "quantity" -> number,
      "product" -> longNumber,
    )(CreateProductQuantityForm.apply)(CreateProductQuantityForm.unapply)
  }

  val updateProductQuantityForm: Form[UpdateProductQuantityForm] = Form {
    mapping(
      "id" -> longNumber,
      "size" -> number,
      "quantity" -> number,
      "product" -> longNumber,
    )(UpdateProductQuantityForm.apply)(UpdateProductQuantityForm.unapply)
  }


  def getAll: Action[AnyContent] = Action.async { implicit request =>
    val productQuantities = pqRepo.list()
    productQuantities.map(ps => Ok(views.html.pqs(ps)))
  }

  def get(id: Long): Action[AnyContent] = Action.async { implicit request =>
    val productQuantity = pqRepo.getByIdOption(id)
    productQuantity.map {
      case Some(p) => Ok(views.html.pq(p))
      case None => Redirect(routes.ProductQuantityController.getAll())
    }
  }

  def delete(id: Long): Action[AnyContent] = Action {
    pqRepo.delete(id)
    Redirect("/productQuantities")
  }

  def update(id: Long): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    var product: Seq[Product] = Seq[Product]()

    val productes: Unit = productRepo.list().onComplete {
      case Success(a) => product = a
      case Failure(_) => print("fail")
    }

    val productQuantity = pqRepo.getById(id)
    productQuantity.map(productQuantity => {
      val prodForm = updateProductQuantityForm.fill(UpdateProductQuantityForm(productQuantity.id, productQuantity.size, productQuantity.quantity, productQuantity.product_id))
      Ok(views.html.pqupdate(prodForm, product))
    })
  }

  def updateHandle(): Action[AnyContent] = Action.async { implicit request =>
    var product: Seq[Product] = Seq[Product]()

    val productes: Unit = productRepo.list().onComplete {
      case Success(a) => product = a
      case Failure(_) => print("fail")
    }

    updateProductQuantityForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.pqupdate(errorForm, product))
        )
      },
      productQuantity => {
        pqRepo.update(productQuantity.id, ProductQuantity(productQuantity.id, productQuantity.size, productQuantity.quantity, productQuantity.product_id)).map { _ =>
          Redirect(routes.ProductQuantityController.update(productQuantity.id)).flashing("success" -> "productQuantity updated")
        }
      }
    )

  }

  def add(): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    val products = productRepo.list()
    products.map(usr => Ok(views.html.pqadd(pqForm, usr)))
  }

  def addHandle(): Action[AnyContent] = Action.async { implicit request =>
    var product: Seq[Product] = Seq[Product]()
    val productes: Unit = productRepo.list().onComplete {
      case Success(a) => product = a
      case Failure(_) => print("fail")
    }


    pqForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.pqadd(errorForm, product))
        )
      },
      productQuantity => {
        pqRepo.create(productQuantity.size, productQuantity.quantity, productQuantity.product_id).map { _ =>
          Redirect(routes.ProductQuantityController.add()).flashing("success" -> "productQuantity.created")
        }
      }
    )

  }

  def addJSON(size: Int, quantity: Int, product_id: Long): Action[AnyContent] = Action.async { implicit request =>
    pqRepo.create(size, quantity,product_id).map {
      res => Ok(Json.toJson(res))
    }
  }

  def getAllJSON: Action[AnyContent] = Action.async { implicit request =>
    val productQuantities = pqRepo.list()
    productQuantities.map(productQuantities => Ok(Json.toJson(productQuantities)))
  }

  def getJSON(id: Long): Action[AnyContent] = Action.async { implicit request =>
    val productQuantity = pqRepo.getByIdOption(id)
    productQuantity.map {
      case productQuantity@Some(p) => Ok(Json.toJson(productQuantity))
      case None => Redirect(routes.ProductQuantityController.getAllJSON)
    }
  }

  def deleteJSON(id: Long): Action[AnyContent] = Action.async { implicit request =>
    pqRepo.delete(id).map {
      _ => Ok(Json.toJson(id))
    }
  }

  def updateJSON(id: Long, size: Int, quantity: Int, product_id: Long): Action[AnyContent] = Action.async { implicit request =>
    pqRepo.update(id, new ProductQuantity(id, size, quantity, product_id)).map {
      _ => Ok(Json.toJson(id))
    }
  }

}

case class CreateProductQuantityForm(size: Int, quantity: Int, product_id: Long)

case class UpdateProductQuantityForm(id: Long, size: Int, quantity: Int, product_id: Long)