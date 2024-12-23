package api

import sttp.tapir._
import sttp.tapir.generic.auto._
import sttp.tapir.json.zio._
import models._
import sttp.model.StatusCode
import sttp.tapir.ztapir._

object TripEndpoints:
  case class ErrorResponse(message: String)

  val registerEndpoint = endpoint
    .post
    .in("api" / "register")
    .in(jsonBody[UserCreate])
    .out(jsonBody[User])
    .errorOut(statusCode and jsonBody[ErrorResponse])

  val loginEndpoint = endpoint
    .post
    .in("api" / "login")
    .in(jsonBody[UserLogin])
    .out(jsonBody[String])
    .errorOut(statusCode and jsonBody[ErrorResponse])

  val createTripEndpoint = endpoint
    .post
    .in("api" / "trips")
    .in(auth.bearer[String]())
    .in(jsonBody[TripCreate])
    .out(jsonBody[Trip])
    .errorOut(statusCode and jsonBody[ErrorResponse])

  val getUserTripsEndpoint = endpoint
    .get
    .in("api" / "trips" / "user")
    .in(auth.bearer[String]())
    .out(jsonBody[TripStats])
    .errorOut(statusCode and jsonBody[ErrorResponse])

  val getTotalStatsEndpoint = endpoint
    .get
    .in("api" / "trips" / "total")
    .out(jsonBody[TripStats])
    .errorOut(statusCode and jsonBody[ErrorResponse])
