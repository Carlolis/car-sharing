package services

import models.{PersonEdge, Trip, TripCreate, TripStats}
import zio._
import java.time.LocalDate

case class TripServiceEdgeDb(edgeDb: EdgeDbDriverLive) extends TripService {
  // TODO: Implement actual database storage
  private var trips: List[Trip] = List.empty

  override def createTrip(tripCreate: TripCreate, userId: Long): Task[Trip] = {
    edgeDb
      .querySingle(
        classOf[PersonEdge],
        """
          | select PersonEdge { name, id }
          |"""
      )
      .flatMap(person =>
        println(s"Person inserted ${person.get(1).name}")
        ZIO.succeed {
          val newTrip = Trip(
            id = Some(trips.length + 1L),
            distance = tripCreate.distance,
            date = tripCreate.date,
            name = tripCreate.name,
            persons = List.empty
          )
          trips = trips :+ newTrip
          newTrip
        }
      )
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

object TripServiceEdgeDb:
  val layer: ZLayer[EdgeDbDriverLive, Nothing, TripService] =
    ZLayer.fromFunction(TripServiceEdgeDb(_))
