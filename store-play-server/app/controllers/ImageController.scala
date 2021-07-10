package controllers

import com.google.inject.Inject
import com.google.inject.Singleton
import models.{Product, ProductRepository, Image, ImageRepository}
import play.api.data.Form
import play.api.data.Forms.{longNumber, mapping, nonEmptyText, number}
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success};

@Singleton
class ImageController @Inject()(imagesRepo: ImageRepository, productRepo: ProductRepository, cc: MessagesControllerComponents)(implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {
  val imageForm: Form[CreateImageForm] = Form {
    mapping(
      "url" -> nonEmptyText,
      "product" -> longNumber,
    )(CreateImageForm.apply)(CreateImageForm.unapply)
  }

  val updateForm: Form[UpdateImageForm] = Form {
    mapping(
      "id" -> longNumber,
      "url" -> nonEmptyText,
      "product" -> longNumber,
    )(UpdateImageForm.apply)(UpdateImageForm.unapply)
  }


  def getAll: Action[AnyContent] = Action.async { implicit request =>
    val images = imagesRepo.list()
    images.map(ps => Ok(views.html.images(ps)))
  }

  def get(id: Long): Action[AnyContent] = Action.async { implicit request =>
    val image = imagesRepo.getByIdOption(id)
    image.map {
      case Some(p) => Ok(views.html.image(p))
      case None => Redirect(routes.ImageController.getAll())
    }
  }


  def delete(id: Long): Action[AnyContent] = Action {
    imagesRepo.delete(id)
    Redirect("/images")
  }

  def update(id: Long): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    var prod: Seq[Product] = Seq[Product]()
    val products: Unit = productRepo.list().onComplete {
      case Success(p) => prod = p
      case Failure(_) => print("fail")
    }

    val image = imagesRepo.getById(id)
    image.map(image => {
      val prodForm = updateForm.fill(UpdateImageForm(image.id, image.url, image.product_id))
      Ok(views.html.imageupdate(prodForm, prod))
    })
  }

  def updateHandle(): Action[AnyContent] = Action.async { implicit request =>
    var prod: Seq[Product] = Seq[Product]()
    val products: Unit = productRepo.list().onComplete {
      case Success(p) => prod = p
      case Failure(_) => print("fail")
    }

    updateForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.imageupdate(errorForm, prod))
        )
      },
      image => {
        imagesRepo.update(image.id, Image(image.id, image.url, image.product_id)).map { _ =>
          Redirect(routes.ImageController.update(image.id)).flashing("success" -> "image updated")
        }
      }
    )

  }

  def add(): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    val products = productRepo.list()
    products.map(p => Ok(views.html.imageadd(imageForm, p)))
  }

  def addHandle(): Action[AnyContent] = Action.async { implicit request =>
    var prod: Seq[Product] = Seq[Product]()
    val products: Unit = productRepo.list().onComplete {
      case Success(p) => prod = p
      case Failure(_) => print("fail")
    }

    imageForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.imageadd(errorForm, prod))
        )
      },
      image => {
        imagesRepo.create(image.url, image.product_id).map { _ =>
          Redirect(routes.ImageController.add()).flashing("success" -> "image.created")
        }
      }
    )

  }

  def addJSON(url: String, product_id: Long): Action[AnyContent] = Action.async { implicit request =>
    imagesRepo.create(url, product_id).map {
      res => Ok(Json.toJson(res))
    }
  }

  def getAllJSON: Action[AnyContent] = Action.async { implicit request =>
    val images = imagesRepo.list()
    images.map(images => Ok(Json.toJson(images)))
  }

  def getJSON(id: Long): Action[AnyContent] = Action.async { implicit request =>
    val image = imagesRepo.getByIdOption(id)
    image.map {
      case image@Some(p) => Ok(Json.toJson(image))
      case None => Redirect(routes.ImageController.getAllJSON)
    }
  }

  def getAllProductJSON(id: Long): Action[AnyContent] = Action.async { implicit request =>
    val image = imagesRepo.getByProduct(id)
    image.map(images => Ok(Json.toJson(images)))
  }

  def deleteJSON(id: Long): Action[AnyContent] = Action.async { implicit request =>
    imagesRepo.delete(id).map {
      _ => Ok(Json.toJson(id))
    }
  }

  def updateJSON(id: Long, url: String, product_id: Long): Action[AnyContent] = Action.async { implicit request =>
    imagesRepo.update(id, new Image(id, url, product_id)).map {
      _ => Ok(Json.toJson(id))
    }
  }

}

case class CreateImageForm(url: String, product_id: Long)

case class UpdateImageForm(id: Long, url: String, product_id: Long)