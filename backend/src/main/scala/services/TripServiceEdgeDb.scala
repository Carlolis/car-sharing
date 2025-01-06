package services

import models._
import zio._
import java.time.LocalDate
import java.util.UUID
import io.scalaland.chimney.dsl._

case class TripServiceEdgeDb(edgeDb: EdgeDbDriverLive) extends TripService {
  // TODO: Implement actual database storage
  private var trips: List[Trip] = List.empty
  private var knownPersons =
    Set(Person("Maé"), Person("Brigitte"), Person("Charles"))

  override def createTrip(
      tripCreate: TripCreate,
      persons: Set[Person]
  ): Task[UUID] = {
    println(
      s"Creating trip ${tripCreate.name} with ${persons}  and ${tripCreate}"
    )
    edgeDb
      .querySingle(
        classOf[UUID],
        s"""
          | with new_trip := (insert TripEdge { name := "tot", distance := 123, date := cal::to_local_date(2024, 10, 10), drivers := (select detached default::PersonEdge filter .name = <str>'Maé') }) select new_trip.id;
          |"""
      )

  }

  override def getUserTrips(personName: String): Task[TripStats] = {
    edgeDb
      .query(
        classOf[TripEdge],
        s"""
          | select TripEdge { id, distance, date, name, drivers: { name } } filter .drivers.name = <str>'$personName'  ;
          |"""
      )
      .map(tripEdge => {
        println(tripEdge)
        // val totalKm = tripEdge.map(_.distance).sum
        // val trips = tripEdge.map(_.transformInto[Trip])
        TripStats(List.empty, 1000)
      })
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
