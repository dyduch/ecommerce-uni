package models

import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ImageRepository @Inject() (dbConfigProvider: DatabaseConfigProvider) (implicit ec: ExecutionContext) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  private class ImageTable(tag: Tag) extends Table[Image](tag, "image") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def url = column[String]("url")

    def product_id = column[Long]("product_id")

    override def * = (id, url, product_id) <> ((Image.apply _).tupled, Image.unapply)
  }

  private val image_table = TableQuery[ImageTable]

  def create(url: String, product_id: Long): Future[Image] = db.run {
    (image_table.map(image => (image.url, image.product_id))
      returning image_table.map(_.id)
      into { case ((url, product_id), id) => Image(id, url, product_id) }
      ) += (url, product_id)
  }

  def list(): Future[Seq[Image]] = db.run {
    image_table.result
  }

  def getByProduct(product_id: Long): Future[Seq[Image]] = db.run {
    image_table.filter(_.product_id === product_id).result
  }

  def getById(id: Long): Future[Image] = db.run {
    image_table.filter(_.id === id).result.head
  }

  def getByIdOption(id: Long): Future[Option[Image]] = db.run {
    image_table.filter(_.id === id).result.headOption
  }

  def delete(id: Long): Future[Unit] = db.run(image_table.filter(_.id === id).delete).map(_ => ())

  def update(id: Long, new_image: Image): Future[Unit] = {
    val imageToUpdate: Image = new_image.copy(id)
    db.run(image_table.filter(_.id === id).update(imageToUpdate)).map(_ => ())
  }
}