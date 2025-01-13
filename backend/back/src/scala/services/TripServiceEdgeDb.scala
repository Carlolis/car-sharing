package services

import io.scalaland.chimney.dsl.*
import io.scalaland.chimney.javacollections.*
import models.*
import models.Trip.fromTripEdge
import zio.*

import java.util.UUID

case class TripServiceEdgeDb(edgeDb: EdgeDbDriverLive) extends TripService {
  // TODO: Implement actual database storage
  private var trips: List[Trip] = List.empty
  private var knownPersons      =
    Set(Person("Maé"), Person("Brigitte"), Person("Charles"))

  override def createTrip(
    tripCreate: TripCreate,
    persons: Set[Person]
  ): Task[UUID] =
    edgeDb
      .querySingle(
        classOf[UUID],
        s"""
          | with new_trip := (insert TripEdge { name := "tot", distance := 123, date := cal::to_local_date(2024, 10, 10), edgeDrivers := (select detached default::PersonEdge filter .name = <str>'Maé') }) select new_trip.id;
          |"""
      )
      .tap(UUID => ZIO.logInfo(s"Created trip with id: $UUID"))

  override def getUserTrips(personName: String): Task[TripStats] =
    edgeDb
      .query(
        classOf[TripEdge],
        s"""
          | select TripEdge { id, distance, date, name, edgeDrivers: { name } } filter .edgeDrivers.name = <str>'$personName'  ;
          |"""
      )
      .map { tripEdge =>

        val trips   = tripEdge.map(fromTripEdge)
        val totalKm = trips.map(_.distance).sum

        TripStats(trips, totalKm)
      }

  override def getTotalStats: Task[TripStats] = ???

  override def deleteTrip(id: UUID): Task[UUID] =
    edgeDb
      .querySingle(
        classOf[String],
        s"""
           | delete TripEdge filter .id = <uuid>'$id';
           | select '${id}';
           |"""
      )
      .map(id => UUID.fromString(id)).zipLeft(ZIO.logInfo(s"Deleted trip with id: $id"))
}

object TripServiceEdgeDb:
  val layer: ZLayer[EdgeDbDriverLive, Nothing, TripService] =
    ZLayer.fromFunction(TripServiceEdgeDb(_))
