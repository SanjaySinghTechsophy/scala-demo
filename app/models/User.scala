package models
import play.api.libs.json._
import play.api.libs.functional.syntax._

case class User(id: Long, name: String, email: String)

object User {
  implicit val userFormat: Format[User] = (
    (JsPath \ "id").format[Long] and
      (JsPath \ "name").format[String] and
      (JsPath \ "email").format[String]
    )(User.apply, unlift(User.unapply))
}
