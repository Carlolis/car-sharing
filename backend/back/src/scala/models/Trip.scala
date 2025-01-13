package models

import zio.json.*

import java.time.LocalDate
import java.util.UUID
import scala.collection.JavaConverters.*

case class Trip(
  id: UUID,
  distance: Int,
  date: LocalDate,
  name: String,
  drivers: Set[Person]
)

object Trip {
  implicit val encoder: JsonEncoder[Trip]    = DeriveJsonEncoder.gen[Trip]
  implicit val decoder: JsonDecoder[Trip]    = DeriveJsonDecoder.gen[Trip]
  def fromTripEdge(tripEdge: TripEdge): Trip =
    Trip(
      tripEdge.getId,
      tripEdge.getDistance,
      tripEdge.getDate,
      tripEdge.getName,
      tripEdge.getDrivers.asScala.map(p => Person(p.name)).toSet
    )
}

case class TripCreate(
  distance: Int,
  date: LocalDate,
  name: String,
  drivers: Set[Person]
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
