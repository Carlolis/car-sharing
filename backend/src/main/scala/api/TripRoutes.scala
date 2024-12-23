package api

import sttp.tapir.server.ServerEndpoint
import zio._
import api.TripEndpoints._
import sttp.model.StatusCode
import services._
import models._
import sttp.tapir.ztapir.*

object TripRoutes:
  val register: ZServerEndpoint[AuthService with TripService, Any] = 
    TripEndpoints.registerEndpoint.serverLogic { userCreate =>
      ZIO.serviceWithZIO[AuthService](_.register(userCreate))
        .map(Right(_))
        .catchAll(err => ZIO.succeed(Left((StatusCode.BadRequest, ErrorResponse(err.getMessage)))))
    }

  val login: ZServerEndpoint[AuthService with TripService, Any] =
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

  val getTotalStats: ZServerEndpoint[AuthService with TripService, Any] =
    TripEndpoints.getTotalStatsEndpoint.serverLogic { _ =>
      ZIO.serviceWithZIO[TripService](_.getTotalStats)
        .map(Right(_))
        .catchAll(err => ZIO.succeed(Left((StatusCode.BadRequest, ErrorResponse(err.getMessage)))))
    }

  val all: List[ZServerEndpoint[AuthService with TripService, Any]] = List(
    register,
    login,
    createTrip,
    getUserTrips,
    getTotalStats
  )
