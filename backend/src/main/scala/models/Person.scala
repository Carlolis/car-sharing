package models

import zio.json._

case class Person(
    id: Option[Long],
    username: String,
    email: String,
    password: String // Note: password sera hashé avant stockage
)

object Person {
  implicit val encoder: JsonEncoder[Person] = DeriveJsonEncoder.gen[Person]
  implicit val decoder: JsonDecoder[Person] = DeriveJsonDecoder.gen[Person]
}

case class PersonCreate(
    name: String,
    email: String
)

object PersonCreate {
  implicit val encoder: JsonEncoder[PersonCreate] =
    DeriveJsonEncoder.gen[PersonCreate]
  implicit val decoder: JsonDecoder[PersonCreate] =
    DeriveJsonDecoder.gen[PersonCreate]
}

case class PersonLogin(
    username: String,
    password: String
)

object PersonLogin {
  implicit val encoder: JsonEncoder[PersonLogin] =
    DeriveJsonEncoder.gen[PersonLogin]
  implicit val decoder: JsonDecoder[PersonLogin] =
    DeriveJsonDecoder.gen[PersonLogin]
}
