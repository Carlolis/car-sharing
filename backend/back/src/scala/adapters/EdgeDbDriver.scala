package adapters

import com.edgedb.driver.EdgeDBClient
import zio.*

import scala.jdk.CollectionConverters.*
import scala.jdk.FutureConverters.*

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
      ).tapBoth(e => ZIO.logError(s"Query failed: $query" + e.getMessage), _ => ZIO.logInfo(s"Query succeeded: $query"))

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
