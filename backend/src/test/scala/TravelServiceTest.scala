import zio.test._
import zio.test.Assertion._

import zio.{ZIO, Task}
import services._
import models._
import java.time.LocalDate
import zio.ZLayer

object TripServiceTest extends ZIOSpecDefault {
  val tripService = ZIO.service[TripService]
  // def spec = suite("TripServiceTest in memory")(
  //   test("createTrip should create a trip successfully") {
  //     val userId = 1L
  //     val tripCreate =
  //       TripCreate(100, LocalDate.now(), "Business", List(userId))

  //     for {
  //       tripService <- tripService

  //       result <- tripService.createTrip(tripCreate, userId)
  //       tripByUser <- tripService.getUserTrips(userId)

  //     } yield assertTrue(result.distance == 100) && assertTrue(
  //       tripByUser.trips.length == 1
  //     )
  //   }
  // ).provideLayer(TripService.layer)
  def spec = suite("TripServiceTest in EdgeDb")(
    test("createTrip should create a trip successfully") {
      val userId = 1L
      val tripCreate =
        TripCreate(100, LocalDate.now(), "Business", List(userId))

      for {
        tripService <- tripService

        result <- tripService.createTrip(tripCreate, userId)
        tripByUser <- tripService.getUserTrips(userId)

      } yield assertTrue(result.distance == 100) && assertTrue(
        tripByUser.trips.length == 1
      )
    }
  ).provide(TripServiceEdgeDb.layer, EdgeDbDriver.layer)
}
