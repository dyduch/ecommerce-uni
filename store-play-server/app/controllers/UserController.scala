package controllers

import com.google.inject.Inject
import com.google.inject.Singleton
import models.{Address, AddressRepository, User, UserRepository}
import play.api.data.Form
import play.api.data.Forms.{boolean, longNumber, mapping, nonEmptyText, number}
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success};

@Singleton
class UserController @Inject()(usersRepo: UserRepository, addressRepo: AddressRepository, cc: MessagesControllerComponents)(implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {
  val userForm: Form[CreateUserForm] = Form {
    mapping(
      "name" -> nonEmptyText,
      "password" -> nonEmptyText,
      "admin" -> boolean,
      "address" -> longNumber,
    )(CreateUserForm.apply)(CreateUserForm.unapply)
  }

  val updateForm: Form[UpdateUserForm] = Form {
    mapping(
      "id" -> longNumber,
      "name" -> nonEmptyText,
      "password" -> nonEmptyText,
      "admin" -> boolean,
      "address" -> longNumber,
    )(UpdateUserForm.apply)(UpdateUserForm.unapply)
  }


  def getAll: Action[AnyContent] = Action.async { implicit request =>
    val users = usersRepo.list()
    users.map(ps => Ok(views.html.users(ps)))
  }

  def get(id: Long): Action[AnyContent] = Action.async { implicit request =>
    val user = usersRepo.getByIdOption(id)
    user.map {
      case Some(p) => Ok(views.html.user(p))
      case None => Redirect(routes.UserController.getAll())
    }
  }


  def delete(id: Long): Action[AnyContent] = Action {
    usersRepo.delete(id)
    Redirect("/users")
  }

  def update(id: Long): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    var addr: Seq[Address] = Seq[Address]()
    val addresss: Unit = addressRepo.list().onComplete {
      case Success(p) => addr = p
      case Failure(_) => print("fail")
    }

    val user = usersRepo.getById(id)
    user.map(user => {
      val addrForm = updateForm.fill(UpdateUserForm(user.id, user.name, user.password, user.admin, user.address_id))
      Ok(views.html.userupdate(addrForm, addr))
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
          BadRequest(views.html.userupdate(errorForm, addr))
        )
      },
      user => {
        usersRepo.update(user.id, User(user.id, user.name, user.password, user.admin, user.address_id)).map { _ =>
          Redirect(routes.UserController.update(user.id)).flashing("success" -> "user updated")
        }
      }
    )

  }

  def add(): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    val addresss = addressRepo.list()
    addresss.map(p => Ok(views.html.useradd(userForm, p)))
  }

  def addHandle(): Action[AnyContent] = Action.async { implicit request =>
    var addr: Seq[Address] = Seq[Address]()
    val addresss: Unit = addressRepo.list().onComplete {
      case Success(p) => addr = p
      case Failure(_) => print("fail")
    }

    userForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.useradd(errorForm, addr))
        )
      },
      user => {
        usersRepo.create(user.name, user.password, user.admin, user.address_id).map { _ =>
          Redirect(routes.UserController.add()).flashing("success" -> "user.created")
        }
      }
    )

  }

  def addJSON(name: String, password: String, admin: Boolean, address_id: Long): Action[AnyContent] = Action.async { implicit request =>
    usersRepo.create(name, password, admin, address_id).map {
      res => Ok(Json.toJson(res))
    }
  }

  def getAllJSON: Action[AnyContent] = Action.async { implicit request =>
    val users = usersRepo.list()
    users.map(users => Ok(Json.toJson(users)))
  }

  def getJSON(id: Long): Action[AnyContent] = Action.async { implicit request =>
    val user = usersRepo.getByIdOption(id)
    user.map {
      case user@Some(p) => Ok(Json.toJson(user))
      case None => Redirect(routes.UserController.getAllJSON)
    }
  }

  def deleteJSON(id: Long): Action[AnyContent] = Action.async { implicit request =>
    usersRepo.delete(id).map {
      _ => Ok(Json.toJson(id))
    }
  }

  def updateJSON(id: Long, name: String, password: String, admin: Boolean, address_id: Long): Action[AnyContent] = Action.async { implicit request =>
    usersRepo.update(id, new User(id, name, password, admin, address_id)).map {
      _ => Ok(Json.toJson(id))
    }
  }

}

case class CreateUserForm(name: String, password: String, admin: Boolean, address_id: Long)

case class UpdateUserForm(id: Long, name: String, password: String, admin: Boolean, address_id: Long)