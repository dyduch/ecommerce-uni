package controllers

import com.google.inject.Inject
import com.google.inject.Singleton
import models.{AddressRepository, Address}
import play.api.data.Form
import play.api.data.Forms.{longNumber, mapping, nonEmptyText, number}
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AddressController @Inject()(addressRepo: AddressRepository, cc: MessagesControllerComponents)(implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {
  val addressForm: Form[CreateAddressForm] = Form {
    mapping(
      "street" -> nonEmptyText,
      "number" -> nonEmptyText,
      "city" -> nonEmptyText,
      "zipcode" -> nonEmptyText,
      "country" -> nonEmptyText,
    )(CreateAddressForm.apply)(CreateAddressForm.unapply)
  }

  val updateAddressForm: Form[UpdateAddressForm] = Form {
    mapping(
      "id" -> longNumber,
      "street" -> nonEmptyText,
      "number" -> nonEmptyText,
      "city" -> nonEmptyText,
      "zipcode" -> nonEmptyText,
      "country" -> nonEmptyText,
    )(UpdateAddressForm.apply)(UpdateAddressForm.unapply)
  }

  def getAll: Action[AnyContent] = Action.async { implicit request =>
    val addresses = addressRepo.list()
    addresses.map(a => Ok(views.html.addresses(a)))
  }

  def get(id: Long): Action[AnyContent] = Action.async { implicit request =>
    val address = addressRepo.getByIdOption(id)
    address.map {
      case Some(p) => Ok(views.html.address(p))
      case None => Redirect(routes.AddressController.getAll())
    }
  }


  def delete(id: Long): Action[AnyContent] = Action {
    addressRepo.delete(id)
    Redirect("/addresses")
  }

  def update(id: Long): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    val address = addressRepo.getById(id)
    address.map(address => {
      val prodForm = updateAddressForm.fill(UpdateAddressForm(address.id, address.street,address.number, address.city, address.zipcode, address.country))
      Ok(views.html.addressupdate(prodForm))
    })
  }

  def updateHandle(): Action[AnyContent] = Action.async { implicit request =>
    updateAddressForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.addressupdate(errorForm))
        )
      },
      address => {
        addressRepo.update(address.id, Address(address.id, address.street,address.number, address.city, address.zipcode, address.country)).map { _ =>
          Redirect(routes.AddressController.update(address.id)).flashing("success" -> "address updated")
        }
      }
    )

  }

  def add(): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    val addresses = addressRepo.list()
    addresses.map(cat => Ok(views.html.addressadd(addressForm)))
  }

  def addHandle(): Action[AnyContent] = Action.async { implicit request =>
    addressForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.addressadd(errorForm))
        )
      },
      address => {
        addressRepo.create(address.street,address.number, address.city, address.zipcode, address.country).map { _ =>
          Redirect(routes.AddressController.add()).flashing("success" -> "address.created")
        }
      }
    )

  }

  def addJSON(street: String, number: String, city: String, zipcode: String, country: String): Action[AnyContent] = Action.async { implicit request =>
    addressRepo.create(street, number, city, zipcode, country).map {
      res => Ok(Json.toJson(res))
    }
  }

  def getAllJSON: Action[AnyContent] = Action.async { implicit request =>
    val addresses = addressRepo.list()
    addresses.map(addresses => Ok(Json.toJson(addresses)))
  }

  def getJSON(id: Long): Action[AnyContent] = Action.async { implicit request =>
    val address = addressRepo.getByIdOption(id)
    address.map {
      case address@Some(p) => Ok(Json.toJson(address))
      case None => Redirect(routes.AddressController.getAllJSON)
    }
  }

  def deleteJSON(id: Long): Action[AnyContent] = Action.async { implicit request =>
    addressRepo.delete(id).map {
      _ => Ok(Json.toJson(id))
    }
  }

  def updateJSON(id: Long, street: String, number: String, city: String, zipcode: String, country: String): Action[AnyContent] = Action.async { implicit request =>
    addressRepo.update(id, new Address(id, street, number, city, zipcode, country)).map {
      _ => Ok(Json.toJson(id))
    }
  }

}

case class CreateAddressForm(street: String, number: String, city: String, zipcode: String, country: String)

case class UpdateAddressForm(id: Long, street: String, number: String, city: String, zipcode: String, country: String)