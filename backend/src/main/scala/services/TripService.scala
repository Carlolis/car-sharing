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

  override def createTrip(tripCreate: TripCreate, userId: Long): Task[Trip] = {
    ZIO.succeed {
      val newTrip = Trip(
        id = Some(trips.length + 1L),
        kilometers = tripCreate.kilometers,
        date = tripCreate.date,
        tripType = tripCreate.tripType,
        userId = userId
      )
      trips = trips :+ newTrip
      newTrip
    }
  }

  override def getUserTrips(userId: Long): Task[TripStats] = {
    ZIO.succeed {
      val userTrips = trips.filter(_.userId == userId)
      val totalKm = userTrips.map(_.kilometers).sum
      TripStats(userTrips, totalKm)
    }
  }

  override def getTotalStats: Task[TripStats] = {
    ZIO.succeed {
      val totalKm = trips.map(_.kilometers).sum
      TripStats(trips, totalKm)
    }
  }
}

object TripService {
  val layer: ULayer[TripService] = ZLayer.succeed(TripServiceLive())
}
