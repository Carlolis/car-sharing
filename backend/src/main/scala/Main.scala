import zio.*
import zio.http.*
import sttp.tapir.server.ziohttp.{ZioHttpInterpreter, ZioHttpServerOptions}
import api.TripRoutes
import services.{AuthService, TripService}
import sttp.tapir.server.interceptor.cors.CORSConfig.AllowedOrigin
import sttp.tapir.server.interceptor.cors.{CORSConfig, CORSInterceptor}
import sttp.tapir.ztapir.RIOMonadError

object Main extends ZIOAppDefault:
  given RIOMonadError[Any] = new RIOMonadError[Any]
  val options: ZioHttpServerOptions[Any] = ZioHttpServerOptions.customiseInterceptors
    .exceptionHandler(new DefectHandler())
    .corsInterceptor(
      CORSInterceptor.customOrThrow(
        CORSConfig.default.copy(
          allowedOrigin = AllowedOrigin.All
        )
      )
    )
    .decodeFailureHandler(CustomDecodeFailureHandler.create())
    .options

  val httpApp = ZioHttpInterpreter(options).toHttp(
    TripRoutes.all
  )

  override def run = 
    val port = 8080
    for
      _ <- Console.printLine(s"Server starting on http://localhost:$port")
      _ <- Console.printLine(s"Swagger UI available at http://localhost:$port/docs")
      _ <- Server.serve(httpApp).provide(
        Server.defaultWithPort(port),
        AuthService.layer,
        TripService.layer
      )
    yield ()
