import zio.test._
import zio.test.Assertion._

import zio.{ZIO, Task}
import services._
import models._
import java.time.LocalDate
import zio.ZLayer

object TripServiceTest extends ZIOSpecDefault {
  val tripService = ZIO.service[TripService]

  def spec = suiteAll("TripServiceTest in EdgeDb") {
    val personName = "Maé"
    val maé = Person(personName)
    val tripCreate =
      TripCreate(100, LocalDate.now(), "Business", Set(maé))

    test("createTrip should create a trip successfully with Maé") {

      for {
        tripService <- tripService

        UUID <- tripService.createTrip(tripCreate, Set(maé))
        tripByUser <- tripService.getUserTrips(personName)

      } yield assertTrue(UUID != null) && assertTrue(
        tripByUser.trips.length > 1
      )
    }
    test("deleteTrip should delete a trip successfully with Maé") {

      for {
        tripService <- tripService

        UUID <- tripService.createTrip(tripCreate, Set(maé))
        _ <- tripService.deleteTrip(UUID)
        tripByUser <- tripService.getUserTrips(personName)

      } yield assertTrue(UUID != null) && assertTrue(
        tripByUser.trips.length == 0
      )
    }
  }.provide(TripServiceEdgeDb.layer, EdgeDbDriver.layer)
}
