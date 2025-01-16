package api

import api.TripEndpoints.*
import domain.services.person.PersonService
import domain.services.trip.TripService
import sttp.model.StatusCode
import sttp.tapir.swagger.bundle.SwaggerInterpreter
import sttp.tapir.ztapir.*
import zio.*

class TripRoutes(tripService: TripService, personService: PersonService):
  // val register: ZServerEndpoint[Any, Any] =
  //   TripEndpoints.registerEndpoint.serverLogic { userCreate =>
  //     authService
  //       .register(userCreate)
  //       .map(Right(_))
  //       .catchAll(err =>
  //         ZIO.succeed(
  //           Left((StatusCode.BadRequest, ErrorResponse(err.getMessage)))
  //         )
  //       )
  //   }

  // val login: ZServerEndpoint[Any, Any] =
  //   TripEndpoints.loginEndpoint.serverLogic { credentials =>
  //     authService
  //       .login(credentials)
  //       .map(Right(_))
  //       .catchAll(err =>
  //         ZIO.succeed(
  //           Left((StatusCode.Unauthorized, ErrorResponse(err.getMessage)))
  //         )
  //       )
  //   }
  val createTrip: ZServerEndpoint[Any, Any] =
    TripEndpoints.createTripEndpoint.serverLogic {
      case (token, tripCreate) =>
        (for {
          // userOpt <- authService.authenticate(token)
          // user <- ZIO
          //   .fromOption(userOpt)
          //   .orElseFail(new Exception("Unauthorized"))
          uuid <- tripService.createTrip(tripCreate)
        } yield uuid)
          .map(Right(_))
          .catchAll(err => ZIO.left(StatusCode.BadRequest, ErrorResponse(err.getMessage)))
    }

  val getUserTrips: ZServerEndpoint[Any, Any] =
    TripEndpoints.getUserTripsEndpoint.serverLogic { token =>
      (for {
        // userOpt <- authService.authenticate(token)
        // user <- ZIO
        //   .fromOption(userOpt)
        //   .orElseFail(new Exception("Unauthorized"))
        result <- tripService.getUserTrips("Maé")
      } yield result)
        .map(Right(_))
        .catchAll(err => ZIO.left(StatusCode.BadRequest, ErrorResponse(err.getMessage)))
    }

  val getTotalStats: ZServerEndpoint[Any, Any]        =
    TripEndpoints.getTotalStatsEndpoint.serverLogic { _ =>
      tripService
        .getTotalStats
        .map(Right(_))
        .catchAll(err => ZIO.left(StatusCode.BadRequest, ErrorResponse(err.getMessage)))
    }
  val createPersonEndpoint: ZServerEndpoint[Any, Any] =
    TripEndpoints.createPersonEndpoint.serverLogic {
      case (token, personCreate) =>
        (for {
          // userOpt <- authService.authenticate(token)
          // user <- ZIO
          //   .fromOption(userOpt)
          //   .orElseFail(new Exception("Unauthorized"))
          uuid <- personService.createPerson(personCreate)
        } yield uuid)
          .map(Right(_))
          .catchAll(err => ZIO.left(StatusCode.BadRequest, ErrorResponse(err.getMessage)))
    }

  def docsEndpoints(
    apiEndpoints: List[ZServerEndpoint[Any, Any]]
  ): List[ZServerEndpoint[Any, Any]] = SwaggerInterpreter()
    .fromServerEndpoints[Task](apiEndpoints, "realworld-tapir-zio", "0.1.0")

  val endpoints: List[ZServerEndpoint[Any, Any]] = {
    val all = List(
      getTotalStats,
      getUserTrips,
      createTrip
      // login,
      // register
    )
    all ++ docsEndpoints(all)
  }
object TripRoutes:
  val live: ZLayer[TripService & PersonService, Nothing, TripRoutes] =
    ZLayer.fromFunction(new TripRoutes(_, _))
