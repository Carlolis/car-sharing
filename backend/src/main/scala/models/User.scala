package models

import zio.json._

case class User(
  id: Option[Long],
  username: String,
  email: String,
  password: String // Note: password sera hashé avant stockage
)

object User {
  implicit val encoder: JsonEncoder[User] = DeriveJsonEncoder.gen[User]
  implicit val decoder: JsonDecoder[User] = DeriveJsonDecoder.gen[User]
}

case class UserCreate(
  username: String,
  email: String,
  password: String
)

object UserCreate {
  implicit val encoder: JsonEncoder[UserCreate] = DeriveJsonEncoder.gen[UserCreate]
  implicit val decoder: JsonDecoder[UserCreate] = DeriveJsonDecoder.gen[UserCreate]
}

case class UserLogin(
  username: String,
  password: String
)

object UserLogin {
  implicit val encoder: JsonEncoder[UserLogin] = DeriveJsonEncoder.gen[UserLogin]
  implicit val decoder: JsonDecoder[UserLogin] = DeriveJsonDecoder.gen[UserLogin]
}
