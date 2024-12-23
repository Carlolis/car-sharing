package api

import sttp.tapir.server.ServerEndpoint
import zio._
import api.TripEndpoints._
import sttp.model.StatusCode
import services._
import models._
import sttp.tapir.ztapir.*
import sttp.tapir.swagger.bundle.SwaggerInterpreter

class TripRoutes(authService: AuthService, tripService: TripService):
  val register: ZServerEndpoint[Any, Any] =
    TripEndpoints.registerEndpoint.serverLogic { userCreate =>
      authService
        .register(userCreate)
        .map(Right(_))
        .catchAll(err =>
          ZIO.succeed(
            Left((StatusCode.BadRequest, ErrorResponse(err.getMessage)))
          )
        )
    }

  val login: ZServerEndpoint[Any, Any] =
    TripEndpoints.loginEndpoint.serverLogic { credentials =>
      authService
        .login(credentials)
        .map(Right(_))
        .catchAll(err =>
          ZIO.succeed(
            Left((StatusCode.Unauthorized, ErrorResponse(err.getMessage)))
          )
        )
    }

  val createTrip: ZServerEndpoint[Any, Any] =
    TripEndpoints.createTripEndpoint.serverLogic { case (token, tripCreate) =>
      (for {
        userOpt <- authService.authenticate(token)
        user <- ZIO
          .fromOption(userOpt)
          .orElseFail(new Exception("Unauthorized"))
        result <- tripService.createTrip(tripCreate, user.id.get)
      } yield result)
        .map(Right(_))
        .catchAll(err =>
          ZIO.succeed(
            Left((StatusCode.BadRequest, ErrorResponse(err.getMessage)))
          )
        )
    }

  val getUserTrips: ZServerEndpoint[Any, Any] =
    TripEndpoints.getUserTripsEndpoint.serverLogic { token =>
      (for {
        userOpt <- authService.authenticate(token)
        user <- ZIO
          .fromOption(userOpt)
          .orElseFail(new Exception("Unauthorized"))
        result <- tripService.getUserTrips(user.id.get)
      } yield result)
        .map(Right(_))
        .catchAll(err =>
          ZIO.succeed(
            Left((StatusCode.BadRequest, ErrorResponse(err.getMessage)))
          )
        )
    }

  val getTotalStats: ZServerEndpoint[Any, Any] =
    TripEndpoints.getTotalStatsEndpoint.serverLogic { _ =>
      tripService.getTotalStats
        .map(Right(_))
        .catchAll(err =>
          ZIO.succeed(
            Left((StatusCode.BadRequest, ErrorResponse(err.getMessage)))
          )
        )
    }

  def docsEndpoints(
      apiEndpoints: List[ZServerEndpoint[Any, Any]]
  ): List[ZServerEndpoint[Any, Any]] = SwaggerInterpreter()
    .fromServerEndpoints[Task](apiEndpoints, "realworld-tapir-zio", "0.1.0")

  val endpoints: List[ZServerEndpoint[Any, Any]] = {
    val all = List(
      getTotalStats,
      getUserTrips,
      createTrip,
      login,
      register
    )
    all ++ docsEndpoints(all)
  }

object TripRoutes:
  val live: ZLayer[AuthService & TripService, Nothing, TripRoutes] =
    ZLayer.fromFunction(new TripRoutes(_, _))
