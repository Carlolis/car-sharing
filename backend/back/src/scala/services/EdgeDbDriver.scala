package services

import zio._
import com.edgedb.driver.EdgeDBClient;
import models.Trip

import java.util.concurrent.CompletionStage
import scala.jdk.FutureConverters.*
import scala.jdk.CollectionConverters.*
import com.edgedb.driver.EdgeDBConnection
import com.edgedb.driver.TLSSecurityMode

case class EdgeDbDriverLive() {
  // val connection = EdgeDBConnection
  //   .builder()
  //   .withDatabase(
  //     "backend"
  //   )
  //   .withHostname("localhost")
  //   .withPort(10700)
  //   .withTlsSecurity(TLSSecurityMode.INSECURE)
  //   .build()

  private var client = new EdgeDBClient()

  def querySingle[A](
      cls: Class[A],
      query: String
  ): Task[A] =
    ZIO
      .fromCompletionStage(
        client
          .querySingle(cls, query.stripMargin)
      )
      .tapError(e => ZIO.logError(s"Query failed: $query" + e.getMessage()))

  def query[A](
      cls: Class[A],
      query: String
  ): Task[List[A]] =
    ZIO
      .fromCompletionStage(
        client
          .query(cls, query.stripMargin)
          .thenApply(javaList => javaList.asScala.toList)
      )

}

object EdgeDbDriver {
  val layer: ULayer[EdgeDbDriverLive] = ZLayer.succeed(EdgeDbDriverLive())
}
