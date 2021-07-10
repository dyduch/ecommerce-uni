package controllers

import com.google.inject.Inject
import com.google.inject.Singleton
import models.{Address, AddressRepository, Supplier, SupplierRepository}
import play.api.data.Form
import play.api.data.Forms.{longNumber, mapping, nonEmptyText, number}
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success};

@Singleton
class SupplierController @Inject()(suppliersRepo: SupplierRepository, addressRepo: AddressRepository, cc: MessagesControllerComponents)(implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {
  val supplierForm: Form[CreateSupplierForm] = Form {
    mapping(
      "name" -> nonEmptyText,
      "address" -> longNumber,
    )(CreateSupplierForm.apply)(CreateSupplierForm.unapply)
  }

  val updateForm: Form[UpdateSupplierForm] = Form {
    mapping(
      "id" -> longNumber,
      "name" -> nonEmptyText,
      "address" -> longNumber,
    )(UpdateSupplierForm.apply)(UpdateSupplierForm.unapply)
  }


  def getAll: Action[AnyContent] = Action.async { implicit request =>
    val suppliers = suppliersRepo.list()
    suppliers.map(ps => Ok(views.html.suppliers(ps)))
  }

  def get(id: Long): Action[AnyContent] = Action.async { implicit request =>
    val supplier = suppliersRepo.getByIdOption(id)
    supplier.map {
      case Some(p) => Ok(views.html.supplier(p))
      case None => Redirect(routes.SupplierController.getAll())
    }
  }


  def delete(id: Long): Action[AnyContent] = Action {
    suppliersRepo.delete(id)
    Redirect("/suppliers")
  }

  def update(id: Long): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    var addr: Seq[Address] = Seq[Address]()
    val addresss: Unit = addressRepo.list().onComplete {
      case Success(p) => addr = p
      case Failure(_) => print("fail")
    }

    val supplier = suppliersRepo.getById(id)
    supplier.map(supplier => {
      val addrForm = updateForm.fill(UpdateSupplierForm(supplier.id, supplier.name, supplier.address_id))
      Ok(views.html.supplierupdate(addrForm, addr))
    })
  }

  def updateHandle(): Action[AnyContent] = Action.async { implicit request =>
    var addr: Seq[Address] = Seq[Address]()
    val addresss: Unit = addressRepo.list().onComplete {
      case Success(p) => addr = p
      case Failure(_) => print("fail")
    }

    updateForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.supplierupdate(errorForm, addr))
        )
      },
      supplier => {
        suppliersRepo.update(supplier.id, Supplier(supplier.id, supplier.name, supplier.address_id)).map { _ =>
          Redirect(routes.SupplierController.update(supplier.id)).flashing("success" -> "supplier updated")
        }
      }
    )

  }

  def add(): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    val addresss = addressRepo.list()
    addresss.map(p => Ok(views.html.supplieradd(supplierForm, p)))
  }

  def addHandle(): Action[AnyContent] = Action.async { implicit request =>
    var addr: Seq[Address] = Seq[Address]()
    val addresss: Unit = addressRepo.list().onComplete {
      case Success(p) => addr = p
      case Failure(_) => print("fail")
    }

    supplierForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.supplieradd(errorForm, addr))
        )
      },
      supplier => {
        suppliersRepo.create(supplier.name, supplier.address_id).map { _ =>
          Redirect(routes.SupplierController.add()).flashing("success" -> "supplier.created")
        }
      }
    )

  }

  def addJSON(name: String, address_id: Long): Action[AnyContent] = Action.async { implicit request =>
    suppliersRepo.create(name, address_id).map {
      res => Ok(Json.toJson(res))
    }
  }

  def getAllJSON: Action[AnyContent] = Action.async { implicit request =>
    val suppliers = suppliersRepo.list()
    suppliers.map(suppliers => Ok(Json.toJson(suppliers)))
  }

  def getJSON(id: Long): Action[AnyContent] = Action.async { implicit request =>
    val supplier = suppliersRepo.getByIdOption(id)
    supplier.map {
      case supplier@Some(p) => Ok(Json.toJson(supplier))
      case None => Redirect(routes.SupplierController.getAllJSON)
    }
  }

  def deleteJSON(id: Long): Action[AnyContent] = Action.async { implicit request =>
    suppliersRepo.delete(id).map {
      _ => Ok(Json.toJson(id))
    }
  }

  def updateJSON(id: Long, name: String, address_id: Long): Action[AnyContent] = Action.async { implicit request =>
    suppliersRepo.update(id, new Supplier(id, name, address_id)).map {
      _ => Ok(Json.toJson(id))
    }
  }

}

case class CreateSupplierForm(name: String, address_id: Long)

case class UpdateSupplierForm(id: Long, name: String, address_id: Long)