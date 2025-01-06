import zio.test._
import zio.test.Assertion._

import zio.{ZIO, Task}
import services._
import models._
import java.time.LocalDate
import zio.ZLayer

object TripServiceTest extends ZIOSpecDefault {
  val tripService = ZIO.service[TripService]

  def spec = suite("TripServiceTest in EdgeDb")(
    test("createTrip should create a trip successfully with Maé") {

      val personName = "Maé"
      val maé = Person(personName)
      val tripCreate =
        TripCreate(100, LocalDate.now(), "Business", Set(maé))
      for {
        tripService <- tripService

        uuid <- tripService.createTrip(tripCreate, Set(maé))
        tripByUser <- tripService.getUserTrips(personName)

      } yield assertTrue(uuid != null) && assertTrue(
        tripByUser.trips.length == 1
      )
    }
  ).provide(TripServiceEdgeDb.layer, EdgeDbDriver.layer)
}
