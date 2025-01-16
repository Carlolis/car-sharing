import adapters.EdgeDbDriver
import domain.models.{PersonCreate, TripCreate}
import domain.services.person.PersonService
import domain.services.person.edgedb.PersonServiceEdgeDb
import domain.services.trip.TripService
import domain.services.trip.edgedb.TripServiceEdgeDb
import zio.test.*
import zio.test.Assertion.*
import zio.{ZIO, ZLayer}

import java.time.LocalDate

object TripServiceTest extends ZIOSpecDefault {
  var personName = "Maé"
  var maé        = PersonCreate(personName)
  var tripCreate =
    TripCreate(100, LocalDate.now(), "Business", Set(maé))

  def spec =
    (suiteAll("TripServiceTest in EdgeDb") {

      test("Maé createTrip should create a trip successfully with Maé") {

        for {

          UUID       <- TripService.createTrip(tripCreate)
          tripByUser <- TripService.getUserTrips(personName)

        } yield assertTrue(UUID != null, tripByUser.trips.length == 1)
      }
      test("Charles createTrip should create a trip successfully with Charles") {
        var personName = "Charles"
        var charles    = PersonCreate(personName)
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
        .after {
          var allPersons = Set(PersonCreate("Maé"), PersonCreate("Brigitte"), PersonCreate("Charles"))
          (for {

            allTrips <- ZIO.foreachPar(allPersons)(person => TripService.getUserTrips(person.name).map(_.trips)).map(_.flatten)
            _        <- ZIO
                          .foreachDiscard(allTrips)(trip => TripService.deleteTrip(trip.id))

          } yield ()).catchAll(e => ZIO.logError(e.getMessage))

        }
      @@ TestAspect
        .before {
          var allPersons = Set(PersonCreate("Maé"), PersonCreate("Brigitte"), PersonCreate("Charles"))
          ZIO.foreachPar(allPersons)(person => PersonService.createPerson(person)).catchAll(e => ZIO.logError(e.getMessage))

        }
      @@ TestAspect.sequential).provideShared(
      TripServiceEdgeDb.layer,
      PersonServiceEdgeDb.layer,
      EdgeDbDriver.layer
    )
}
