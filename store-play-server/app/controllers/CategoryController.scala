package controllers

import com.google.inject.Inject
import com.google.inject.Singleton
import models.{Category, CategoryRepository }
import play.api.data.Form
import play.api.data.Forms.{mapping, nonEmptyText, number}
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CategoryController @Inject()(categoryRepo: CategoryRepository, cc: MessagesControllerComponents)(implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {
  val categoryForm: Form[CreateCategoryForm] = Form {
    mapping(
      "name" -> nonEmptyText,
    )(CreateCategoryForm.apply)(CreateCategoryForm.unapply)
  }

  val updateCategoryForm: Form[UpdateCategoryForm] = Form {
    mapping(
      "id" -> number,
      "name" -> nonEmptyText,
    )(UpdateCategoryForm.apply)(UpdateCategoryForm.unapply)
  }

  def getCategories: Action[AnyContent] = Action.async { implicit request =>
    val categories = categoryRepo.list()
    categories.map(cat => Ok(views.html.categories(cat)))
  }

  def getCategory(id: Int): Action[AnyContent] = Action.async { implicit request =>
    val category = categoryRepo.getByIdOption(id)
    category.map {
      case Some(p) => Ok(views.html.category(p))
      case None => Redirect(routes.CategoryController.getCategories())
    }
  }


  def delete(id: Int): Action[AnyContent] = Action {
    categoryRepo.delete(id)
    Redirect("/categories")
  }

  def updateCategory(id: Int): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    val category = categoryRepo.getById(id)
    category.map(category => {
      val prodForm = updateCategoryForm.fill(UpdateCategoryForm(category.id, category.name))
      Ok(views.html.categoryupdate(prodForm))
    })
  }

  def updateCategoryHandle(): Action[AnyContent] = Action.async { implicit request =>
    updateCategoryForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.categoryupdate(errorForm))
        )
      },
      category => {
        categoryRepo.update(category.id, Category(category.id, category.name)).map { _ =>
          Redirect(routes.CategoryController.updateCategory(category.id)).flashing("success" -> "category updated")
        }
      }
    )

  }

  def addCategory(): Action[AnyContent] = Action.async { implicit request: MessagesRequest[AnyContent] =>
    val categories = categoryRepo.list()
    categories.map(cat => Ok(views.html.categoryadd(categoryForm)))
  }

  def addCategoryHandle(): Action[AnyContent] = Action.async { implicit request =>
    categoryForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.categoryadd(errorForm))
        )
      },
      category => {
        categoryRepo.create(category.name).map { _ =>
          Redirect(routes.CategoryController.addCategory()).flashing("success" -> "category.created")
        }
      }
    )

  }

  def addCategoryJSON(name: String): Action[AnyContent] = Action.async { implicit request =>
    categoryRepo.create(name).map {
      res => Ok(Json.toJson(res))
    }
  }

  def getCategoriesJSON: Action[AnyContent] = Action.async { implicit request =>
    val categories = categoryRepo.list()
    categories.map(categories => Ok(Json.toJson(categories)))
  }

  def getCategoryJSON(id: Int): Action[AnyContent] = Action.async { implicit request =>
    val category = categoryRepo.getByIdOption(id)
    category.map {
      case category@Some(p) => Ok(Json.toJson(category))
      case None => Redirect(routes.CategoryController.getCategoriesJSON)
    }
  }

  def deleteJSON(id: Int): Action[AnyContent] = Action.async { implicit request =>
    categoryRepo.delete(id).map {
      _ => Ok(Json.toJson(id))
    }
  }

  def updateCategoryJSON(id: Int, name: String): Action[AnyContent] = Action.async { implicit request =>
    categoryRepo.update(id, new Category(id, name)).map {
      _ => Ok(Json.toJson(id))
    }
  }

}

case class CreateCategoryForm(name: String)

case class UpdateCategoryForm(id: Int, name: String)