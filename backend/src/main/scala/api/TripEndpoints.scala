package api

import sttp.tapir.ztapir._
import sttp.tapir.generic.auto._
import sttp.tapir.json.zio._
import models._
import sttp.model.StatusCode
import zio.json._
import java.util.UUID

object TripEndpoints:
  case class ErrorResponse(messge: String) derives JsonEncoder, JsonDecoder

  // val registerEndpoint = endpoint.post
  //   .in("api" / "register")
  //   .in(jsonBody[UserCreate])
  //   .out(jsonBody[Person])
  //   .errorOut(statusCode and jsonBody[ErrorResponse])

  // val loginEndpoint = endpoint.post
  //   .in("api" / "login")
  //   .in(jsonBody[UserLogin])
  //   .out(jsonBody[String])
  //   .errorOut(statusCode and jsonBody[ErrorResponse])

  val createTripEndpoint = endpoint.post
    .in("api" / "trips")
    .in(auth.bearer[String]())
    .in(jsonBody[TripCreate])
    .out(jsonBody[UUID])
    .errorOut(statusCode and jsonBody[ErrorResponse])

  val getUserTripsEndpoint = endpoint.get
    .in("api" / "trips" / "user")
    .in(auth.bearer[String]())
    .out(jsonBody[TripStats])
    .errorOut(statusCode and jsonBody[ErrorResponse])

  val getTotalStatsEndpoint = endpoint.get
    .in("api" / "trips" / "total")
    .out(jsonBody[TripStats])
    .errorOut(statusCode and jsonBody[ErrorResponse])
