package models

import zio.json._
import java.time.LocalDate
import com.edgedb.driver.annotations.EdgeDBType
import com.edgedb.driver.annotations.EdgeDBDeserializer
import java.util
import com.edgedb.driver.annotations.EdgeDBLinkType
import java.util.UUID

@EdgeDBType
case class PersonEdge @EdgeDBDeserializer() (name: String)

@EdgeDBType
class TripEdge @EdgeDBDeserializer() (
    id: UUID,
    distance: Int,
    date: LocalDate,
    name: String,
    @EdgeDBLinkType(classOf[PersonEdge])
    drivers: util.Collection[PersonEdge]
) {
  def getId: UUID = id
  def getDistance: Int = distance
  def getDate: LocalDate = date
  def getName: String = name
  def getDrivers: util.Collection[PersonEdge] = drivers
}

case class TripEdgeCreate(
    distance: Double,
    date: LocalDate,
    name: String,
    drivers: util.Collection[Person]
)

case class TripEdgeStats(
    trips: util.Collection[Trip],
    totalKilometers: Double
)
