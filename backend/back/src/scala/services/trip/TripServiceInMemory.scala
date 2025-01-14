package services.trip

import models.{Person, Trip, TripCreate, TripStats}
import zio.{Task, ULayer, ZIO, ZLayer}

import java.util.UUID

case class TripServiceInMemory() extends TripService {
  // TODO: Implement actual database storage
  private var trips: List[Trip] = List.empty

  private var knownPersons =
    Set(Person("Maé"), Person("Brigitte"), Person("Charles"))

  override def createTrip(
                           tripCreate: TripCreate
                           
                         ): Task[UUID] =
    if (!tripCreate.drivers.subsetOf(knownPersons))
      ZIO.fail(new Exception("Unknown person"))
    else
      ZIO
        .succeed {
          val newTrip = Trip(
            id = UUID.randomUUID(),
            distance = tripCreate.distance,
            date = tripCreate.date,
            name = tripCreate.name,
            drivers= tripCreate.drivers
          )
          trips = trips :+ newTrip
          newTrip
        }.as(UUID.randomUUID())

  override def getUserTrips(name: String): Task[TripStats] =
    ZIO.succeed {
      val userTrips =
        trips.filter(trip => true
          // trip.drivers.flatMap(person => person.name).contains(name)
        )
      val totalKm   = userTrips.map(_.distance).sum
      TripStats(userTrips, totalKm)
    }

  override def getTotalStats: Task[TripStats] =
    ZIO.succeed {
      val totalKm = trips.map(_.distance).sum
      TripStats(trips, totalKm)
    }

  override def deleteTrip(id: UUID): Task[UUID] = ???

  override def updateTrip(tripUpdate: Trip): Task[UUID] = ???
}

object TripServiceInMemory {
  val layer: ULayer[TripService] = ZLayer.succeed(TripServiceInMemory())
}