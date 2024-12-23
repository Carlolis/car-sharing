package api

import sttp.tapir.server.ServerEndpoint
import zio.*
import api.CarEndpoints.*
import sttp.model.StatusCode
import sttp.tapir.swagger.bundle.SwaggerInterpreter
import sttp.tapir.ztapir.*

import scala.collection.immutable.List

object CarRoutes:
  // Simulated in-memory storage
  private var cars = Map[String, Car]()

/*  val getCars: ZServerEndpoint[Any, Any] =
    CarEndpoints.getCarsEndpoint.serverLogic { _ =>
      ZIO.succeed(cars.values.toList)
    }*/

  val getCar: ZServerEndpoint[Any, Any] =
    CarEndpoints.getCarEndpoint.serverLogic { id =>
      ZIO.succeed(
        cars.get(id) match
          case Some(car) => Right(car)
          case None => Left((StatusCode.NotFound, ErrorResponse(s"Car with id $id not found")))
      )
    }

  val addCar: ZServerEndpoint[Any, Any] =
    CarEndpoints.addCarEndpoint.serverLogic { car =>
      ZIO.succeed {
        cars = cars + (car.id -> car)
        Right(car)
      }
    }

  val updateCar: ZServerEndpoint[Any, Any] =
    CarEndpoints.updateCarEndpoint.serverLogic { case (id, car) =>
      ZIO.succeed(
        if cars.contains(id) then
          cars = cars + (id -> car)
          Right(car)
        else
          Left((StatusCode.NotFound, ErrorResponse(s"Car with id $id not found")))
      )
    }

  val deleteCar: ZServerEndpoint[Any, Any] =
    CarEndpoints.deleteCarEndpoint.serverLogic { id =>
      ZIO.succeed(
        if cars.contains(id) then
          cars = cars - id
          Right(())
        else
          Left((StatusCode.NotFound, ErrorResponse(s"Car with id $id not found")))
      )
    }


  def docsEndpoints(apiEndpoints: List[ZServerEndpoint[Any, Any]]): List[ZServerEndpoint[Any, Any]] = SwaggerInterpreter()
    .fromServerEndpoints[Task](apiEndpoints, "realworld-tapir-zio", "0.1.0")
  val all: List[ZServerEndpoint[Any, Any]] = {
    val api = List(getCar, addCar, updateCar, deleteCar)
    val docs = docsEndpoints(api)
    api ++ docs
  }

