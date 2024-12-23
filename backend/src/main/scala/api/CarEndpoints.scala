package api

import sttp.tapir.*
import sttp.tapir.generic.auto.*
import sttp.tapir.json.zio.*
import sttp.tapir.server.ziohttp.ZioHttpInterpreter
import zio.*
import zio.json.*

object CarEndpoints:
  case class Car(id: String, model: String, available: Boolean) derives JsonEncoder, JsonDecoder
  case class ErrorResponse(message: String) derives JsonEncoder, JsonDecoder

  val getCarsEndpoint = endpoint
    .get
    .in("cars")
    .out(jsonBody[List[Car]])
    .errorOut(statusCode.and(jsonBody[ErrorResponse]))
    .description("Get all available cars")

  val getCarEndpoint = endpoint
    .get
    .in("cars" / path[String]("id"))
    .out(jsonBody[Car])
    .errorOut(statusCode.and(jsonBody[ErrorResponse]))
    .description("Get a car by ID")

  val addCarEndpoint = endpoint
    .post
    .in("cars")
    .in(jsonBody[Car])
    .out(jsonBody[Car])
    .errorOut(statusCode.and(jsonBody[ErrorResponse]))
    .description("Add a new car")

  val updateCarEndpoint = endpoint
    .put
    .in("cars" / path[String]("id"))
    .in(jsonBody[Car])
    .out(jsonBody[Car])
    .errorOut(statusCode.and(jsonBody[ErrorResponse]))
    .description("Update a car")

  val deleteCarEndpoint = endpoint
    .delete
    .in("cars" / path[String]("id"))
    .out(emptyOutput)
    .errorOut(statusCode.and(jsonBody[ErrorResponse]))
    .description("Delete a car")
