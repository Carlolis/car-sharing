import adapters.EdgeDbDriver
import models.*
import services.*
import services.trip.{TripService, *}
import zio.test.*
import zio.test.Assertion.*
import zio.{Task, ZIO, ZLayer}

import java.time.LocalDate

object TripServiceTest extends ZIOSpecDefault {
  var personName = "Maé"
  var maé        = Person(personName)
  var tripCreate =
    TripCreate(100, LocalDate.now(), "Business", Set(maé))
  def spec       =
    (suiteAll("TripServiceTest in EdgeDb") {
      test("Maé createTrip should create a trip successfully with Maé") {

        for {

          UUID       <- TripService.createTrip(tripCreate)
          tripByUser <- TripService.getUserTrips(personName)

        } yield assertTrue(UUID != null, tripByUser.trips.length == 1)
      }
      test("Charles createTrip should create a trip successfully with Charles") {
        var personName = "Charles"
        var charles    = Person(personName)
        for {

          UUID       <- TripService.createTrip(tripCreate.copy(drivers = Set(charles)))
          tripByUser <- TripService.getUserTrips(personName)

        } yield assertTrue(UUID != null, tripByUser.trips.length == 1)
      }
      test("deleteTrip should delete a trip successfully with Maé") {

        for {

          UUID       <- TripService.createTrip(tripCreate)
          _          <- TripService.deleteTrip(UUID)
          tripByUser <- TripService.getUserTrips(personName)

        } yield assertTrue(UUID != null, tripByUser.trips.isEmpty)
      }
    }
      @@ TestAspect
        .before {
          var allPersons = Set(Person("Maé"), Person("Brigitte"), Person("Charles"))
          (for {

            allTrips <- ZIO.foreachPar(allPersons)(person => TripService.getUserTrips(person.name).map(_.trips)).map(_.flatten)
            _        <- ZIO
                          .foreachDiscard(allTrips)(trip => TripService.deleteTrip(trip.id))

          } yield ()).catchAll(e => ZIO.logError(e.getMessage))

        } @@ TestAspect.sequential).provideShared(
      TripServiceEdgeDb.layer,
      EdgeDbDriver.layer
    )
}
