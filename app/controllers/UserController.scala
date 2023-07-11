package controllers
import models.User

import javax.inject.Inject
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

class UserController @Inject()(protected val dbConfigProvider: DatabaseConfigProvider, cc: ControllerComponents)
                              (implicit ec: ExecutionContext) extends AbstractController(cc)
  with HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  private val Users = TableQuery[UsersTable]

  def getUsers: Action[AnyContent] = Action.async { implicit request =>
    val query = Users.result
    val usersFuture = db.run(query)
    usersFuture.map(users => Ok(Json.toJson(users)))
  }

  def getUser(id: Long): Action[AnyContent] = Action.async { implicit request =>
    val query = Users.filter(_.id === id).result.headOption
    val userFuture = db.run(query)
    userFuture.map {
      case Some(user) => Ok(Json.toJson(user))
      case None => NotFound
    }
  }

  def createUser: Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[User].fold(
      errors => Future.successful(BadRequest(Json.obj("message" -> "Invalid user format."))),
      user => {
        val insertQuery = (Users returning Users.map(_.id)) += user
        val insertedUserFuture = db.run(insertQuery).map(id => user.copy(id = id))
        insertedUserFuture.map(insertedUser => Ok(Json.toJson(insertedUser)))
      }
    )
  }

  def updateUser(id: Long): Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[User].fold(
      errors => Future.successful(BadRequest(Json.obj("message" -> "Invalid user format."))),
      user => {
        val updateQuery = Users.filter(_.id === id).update(user.copy(id = id))
        db.run(updateQuery).map(_ => Ok(Json.toJson(user)))
      }
    )
  }

  def deleteUser(id: Long): Action[AnyContent] = Action.async { implicit request =>
    val deleteQuery = Users.filter(_.id === id).delete
    db.run(deleteQuery).map(_ => Ok(Json.obj("message" -> "User deleted.")))
  }

  private class UsersTable(tag: Tag) extends Table[User](tag, "users") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def email = column[String]("email")

    def * = (id, name, email) <> ((User.apply _).tupled, User.unapply)
  }
}