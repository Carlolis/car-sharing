import zio.test._
import zio.test.Assertion._

import zio.{ZIO, Task}
import services._
import models._
import java.time.LocalDate
import zio.ZLayer

object TripServiceTest extends ZIOSpecDefault {
  var tripService = ZIO.service[TripService]
  var personName = "Maé"
  var maé = Person(personName)
  var tripCreate =
    TripCreate(100, LocalDate.now(), "Business", Set(maé))
  def spec =
    (suite("TripServiceTest in EdgeDb")(
      test("createTrip should create a trip successfully with Maé") {

        for {
          tripService <- tripService

          UUID <- tripService.createTrip(tripCreate, Set(maé))
          tripByUser <- tripService.getUserTrips(personName)

        } yield assertTrue(UUID != null) && assertTrue(
          tripByUser.trips.length == 1
        )
      },
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
    )
      @@ TestAspect
        .before {

          var toto = for {
            tripService <- tripService
            trips <- tripService.getUserTrips("Maé")
            _ <- ZIO
              .foreachDiscard(trips.trips)(trip =>
                tripService.deleteTrip(trip.id)
              )

          } yield ()
          toto
            .catchAll(e => ZIO.logError(e.getMessage))

        } @@ TestAspect.sequential).provideShared(
      TripServiceEdgeDb.layer,
      EdgeDbDriver.layer
    )

}
