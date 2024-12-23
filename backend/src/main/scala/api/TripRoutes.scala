package api

import sttp.tapir.server.ServerEndpoint
import zio._
import api.TripEndpoints._
import sttp.model.StatusCode
import services._
import models._

object TripRoutes:
  val register: ZServerEndpoint[AuthService, Any] =
    TripEndpoints.registerEndpoint.serverLogic { userCreate =>
      ZIO.serviceWithZIO[AuthService](_.register(userCreate))
        .map(Right(_))
        .catchAll(err => ZIO.succeed(Left((StatusCode.BadRequest, ErrorResponse(err.getMessage)))))
    }

  val login: ZServerEndpoint[AuthService, Any] =
    TripEndpoints.loginEndpoint.serverLogic { credentials =>
      ZIO.serviceWithZIO[AuthService](_.login(credentials))
        .map(Right(_))
        .catchAll(err => ZIO.succeed(Left((StatusCode.Unauthorized, ErrorResponse(err.getMessage)))))
    }

  val createTrip: ZServerEndpoint[AuthService with TripService, Any] =
    TripEndpoints.createTripEndpoint.serverLogic { case (token, tripCreate) =>
      (for {
        auth <- ZIO.service[AuthService]
        trip <- ZIO.service[TripService]
        userOpt <- auth.authenticate(token)
        user <- ZIO.fromOption(userOpt).orElseFail(new Exception("Unauthorized"))
        result <- trip.createTrip(tripCreate, user.id.get)
      } yield result)
        .map(Right(_))
        .catchAll(err => ZIO.succeed(Left((StatusCode.BadRequest, ErrorResponse(err.getMessage)))))
    }

  val getUserTrips: ZServerEndpoint[AuthService with TripService, Any] =
    TripEndpoints.getUserTripsEndpoint.serverLogic { token =>
      (for {
        auth <- ZIO.service[AuthService]
        trip <- ZIO.service[TripService]
        userOpt <- auth.authenticate(token)
        user <- ZIO.fromOption(userOpt).orElseFail(new Exception("Unauthorized"))
        result <- trip.getUserTrips(user.id.get)
      } yield result)
        .map(Right(_))
        .catchAll(err => ZIO.succeed(Left((StatusCode.BadRequest, ErrorResponse(err.getMessage)))))
    }

  val getTotalStats: ZServerEndpoint[TripService, Any] =
    TripEndpoints.getTotalStatsEndpoint.serverLogic { _ =>
      ZIO.serviceWithZIO[TripService](_.getTotalStats)
        .map(Right(_))
        .catchAll(err => ZIO.succeed(Left((StatusCode.BadRequest, ErrorResponse(err.getMessage)))))
    }

  val all: List[ServerEndpoint[Any, ZIO[AuthService with TripService, Throwable, *]]] = List(
    register,
    login,
    createTrip,
    getUserTrips,
    getTotalStats
  )
