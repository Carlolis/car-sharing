package api

import models.*
import sttp.model.StatusCode
import sttp.tapir.generic.auto.*
import sttp.tapir.json.zio.*
import sttp.tapir.ztapir.*
import zio.json.*

import java.util.UUID
import java.util.Locale

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

  val createTripEndpoint = endpoint
    .post
    .in("api" / "trips")
    .in(auth.bearer[String]())
    .in(jsonBody[TripCreate])
    .out(jsonBody[UUID])
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
