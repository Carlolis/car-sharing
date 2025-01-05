package services

import models._
import zio._
import java.time.LocalDate

trait TripService {
  def createTrip(tripCreate: TripCreate, userId: Long): Task[Trip]
  def getUserTrips(userId: Long): Task[TripStats]
  def getTotalStats: Task[TripStats]
}

case class TripServiceLive() extends TripService {
  // TODO: Implement actual database storage
  private var trips: List[Trip] = List.empty

  private var persons = List(
    Person(Some(1L), "John", "Doe", "john.doe@example.com"),
    Person(Some(2L), "Jane", "Doe", "jane.doe@example.com")
  )

  override def createTrip(tripCreate: TripCreate, userId: Long): Task[Trip] = {
    ZIO.succeed {
      val newTrip = Trip(
        id = Some(trips.length + 1L),
        distance = tripCreate.distance,
        date = tripCreate.date,
        name = tripCreate.name,
        persons =
          persons.filter(person => tripCreate.personIds.contains(person.id.get))
      )
      trips = trips :+ newTrip
      newTrip
    }
  }

  override def getUserTrips(userId: Long): Task[TripStats] = {
    ZIO.succeed {
      val userTrips =
        trips.filter(trip =>
          trip.persons.flatMap(person => person.id).contains(userId)
        )
      val totalKm = userTrips.map(_.distance).sum
      TripStats(userTrips, totalKm)
    }
  }

  override def getTotalStats: Task[TripStats] = {
    ZIO.succeed {
      val totalKm = trips.map(_.distance).sum
      TripStats(trips, totalKm)
    }
  }
}

object TripService {
  val layer: ULayer[TripService] = ZLayer.succeed(TripServiceLive())
}
