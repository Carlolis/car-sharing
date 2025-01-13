package models

import zio.json._
import java.util.UUID

case class Person(
  name: String
) {
  def toPersonEdge: PersonEdge =
    PersonEdge(name)
}

object Person {
  implicit val encoder: JsonEncoder[Person] = DeriveJsonEncoder.gen[Person]
  implicit val decoder: JsonDecoder[Person] = DeriveJsonDecoder.gen[Person]
}
