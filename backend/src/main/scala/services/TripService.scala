package services

import models._
import zio._
import java.time.LocalDate
import java.util.UUID

trait TripService {
  def createTrip(tripCreate: TripCreate, persons: Set[Person]): Task[UUID]
  def getUserTrips(personName: String): Task[TripStats]
  def getTotalStats: Task[TripStats]
}

case class TripServiceLive() extends TripService {
  // TODO: Implement actual database storage
  private var trips: List[Trip] = List.empty

  private var knownPersons =
    Set(Person("Maé"), Person("Brigitte"), Person("Charles"))

  override def createTrip(
      tripCreate: TripCreate,
      persons: Set[Person]
  ): Task[UUID] = {
    if (!persons.subsetOf(knownPersons)) {
      ZIO.fail(new Exception("Unknown person"))
    } else
      ZIO
        .succeed {
          val newTrip = Trip(
            id = UUID.randomUUID(),
            distance = tripCreate.distance,
            date = tripCreate.date,
            name = tripCreate.name,
            persons
          )
          trips = trips :+ newTrip
          newTrip
        }
        .zipRight(ZIO.succeed(UUID.randomUUID()))
  }

  override def getUserTrips(name: String): Task[TripStats] = {
    ZIO.succeed {
      val userTrips =
        trips.filter(trip => true
        // trip.drivers.flatMap(person => person.name).contains(name)
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
