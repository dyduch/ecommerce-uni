package controllers

import com.google.inject.Inject
import com.google.inject.Singleton
import models.{Category, CategoryRepository, Product, ProductRepository}
import play.api.data.Form
import play.api.data.Forms.{longNumber, mapping, nonEmptyText, number}
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success};

@Singleton
class ProductController @Inject()(productsRepo: ProductRepository, categoryRepo: CategoryRepository, cc: MessagesControllerComponents)(implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {
  val productForm: Form[CreateProductForm] = Form {
    mapping(
      "name" -> nonEmptyText,
      "color" -> nonEmptyText,
      "price" -> number,
      "description" -> nonEmptyText,
      "category" -> number,
    )(CreateProductForm.apply)(CreateProductForm.unapply)
  }

  val updateProductForm: Form[UpdateProductForm] = Form {
    mapping(
      "id" -> longNumber,
      "name" -> nonEmptyText,
      "color" -> nonEmptyText,
      "price" -> number,
      "description" -> nonEmptyText,
      "category" -> number,
    )(UpdateProductForm.apply)(UpdateProductForm.unapply)
  }


  def getProducts: Action[AnyContent] = Action.async { implicit request =>
    val products = productsRepo.list()
    products.map(ps => Ok(views.html.products(ps)))
  }

  def getProduct(id: Long): Action[AnyContent] = Action.async { implicit request =>
    val product = productsRepo.getByIdOption(id)
    product.map {
      case Some(p) => Ok(views.html.product(p))
      case None => Redirect(routes.ProductController.getProducts())
    }
  }


  def delete(id: Long): Action[AnyContent] = Action {
    productsRepo.delete(id)
    Redirect("/products")
  }

  def updateProduct(id: Long): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    var categ: Seq[Category] = Seq[Category]()
    val categories: Unit = categoryRepo.list().onComplete {
      case Success(cat) => categ = cat
      case Failure(_) => print("fail")
    }

    val product = productsRepo.getById(id)
    product.map(product => {
      val prodForm = updateProductForm.fill(UpdateProductForm(product.id, product.name, product.color, product.price, product.description, product.category_id))
      Ok(views.html.productupdate(prodForm, categ))
    })
  }

  def updateProductHandle(): Action[AnyContent] = Action.async { implicit request =>
    var categ: Seq[Category] = Seq[Category]()
    val categories: Unit = categoryRepo.list().onComplete {
      case Success(cat) => categ = cat
      case Failure(_) => print("fail")
    }

    updateProductForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.productupdate(errorForm, categ))
        )
      },
      product => {
        productsRepo.update(product.id, Product(product.id, product.name, product.color, product.price, product.description, product.category_id)).map { _ =>
          Redirect(routes.ProductController.updateProduct(product.id)).flashing("success" -> "product updated")
        }
      }
    )

  }

  def addProduct(): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    val categories = categoryRepo.list()
    categories.map(cat => Ok(views.html.productadd(productForm, cat)))
  }

  def addProductHandle(): Action[AnyContent] = Action.async { implicit request =>
    var categ: Seq[Category] = Seq[Category]()
    val categories: Unit = categoryRepo.list().onComplete {
      case Success(cat) => categ = cat
      case Failure(_) => print("fail")
    }

    productForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.productadd(errorForm, categ))
        )
      },
      product => {
        productsRepo.create(product.name, product.color, product.price, product.description, product.category_id).map { _ =>
          Redirect(routes.ProductController.addProduct()).flashing("success" -> "product.created")
        }
      }
    )

  }

  def addProductJSON(name: String, color: String, price: Int, description: String, category_id: Int): Action[AnyContent] = Action.async { implicit request =>
    productsRepo.create(name, color, price, description, category_id).map {
      res => Ok(Json.toJson(res))
    }
  }

  def getProductsJSON: Action[AnyContent] = Action.async { implicit request =>
    val products = productsRepo.list()
    products.map(products => Ok(Json.toJson(products)))
  }

  def getProductJSON(id: Long): Action[AnyContent] = Action.async { implicit request =>
    val product = productsRepo.getByIdOption(id)
    product.map {
      case product@Some(p) => Ok(Json.toJson(product))
      case None => Redirect(routes.ProductController.getProductsJSON)
    }
  }

  def deleteJSON(id: Long): Action[AnyContent] = Action.async { implicit request =>
    productsRepo.delete(id).map {
      _ => Ok(Json.toJson(id))
    }
  }

  def updateProductJSON(id: Long, name: String, color: String, price: Int, description: String, category_id: Int): Action[AnyContent] = Action.async { implicit request =>
    productsRepo.update(id, new Product(id, name, color, price, description, category_id)).map {
      _ => Ok(Json.toJson(id))
    }
  }

}

case class CreateProductForm(name: String, color: String, price: Int, description: String, category_id: Int)

case class UpdateProductForm(id: Long, name: String, color: String, price: Int, description: String, category_id: Int)