package models

import com.edgedb.driver.annotations.{EdgeDBDeserializer, EdgeDBLinkType, EdgeDBType}
import zio.json.*

import java.time.LocalDate
import java.util
import java.util.UUID

@EdgeDBType
case class PersonEdge @EdgeDBDeserializer() (var name: String)
@EdgeDBType
class TripEdge @EdgeDBDeserializer() (
  id: UUID,
  distance: Int,
  date: LocalDate,
  name: String,
  @EdgeDBLinkType(classOf[PersonEdge])
  edgeDrivers: util.Collection[PersonEdge]
) {
  def getId: UUID                             = id
  def getDistance: Int                        = distance
  def getDate: LocalDate                      = date
  def getName: String                         = name
  def getDrivers: util.Collection[PersonEdge] = edgeDrivers
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
