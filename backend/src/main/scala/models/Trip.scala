package models

import zio.json._
import java.time.LocalDate

case class Trip(
    id: Option[Long],
    distance: Double,
    date: LocalDate,
    name: String,
    persons: List[Person]
)

object Trip {
  implicit val encoder: JsonEncoder[Trip] = DeriveJsonEncoder.gen[Trip]
  implicit val decoder: JsonDecoder[Trip] = DeriveJsonDecoder.gen[Trip]
}

case class TripCreate(
    distance: Double,
    date: LocalDate,
    name: String,
    personIds: List[Long]
)

object TripCreate {
  implicit val encoder: JsonEncoder[TripCreate] =
    DeriveJsonEncoder.gen[TripCreate]
  implicit val decoder: JsonDecoder[TripCreate] =
    DeriveJsonDecoder.gen[TripCreate]
}

case class TripStats(
    trips: List[Trip],
    totalKilometers: Double
)

object TripStats {
  implicit val encoder: JsonEncoder[TripStats] =
    DeriveJsonEncoder.gen[TripStats]
  implicit val decoder: JsonDecoder[TripStats] =
    DeriveJsonDecoder.gen[TripStats]
}
