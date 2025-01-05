package services

import zio._
import com.edgedb.driver.EdgeDBClient;
import models.Trip
import scala.jdk.CollectionConverters._
import java.util.concurrent.CompletionStage
import java.lang.String

case class EdgeDbDriverLive() {
  private var client = new EdgeDBClient();

  def querySingle[A](cls: Class[A], query: String): Task[A] = {

    ZIO.fromCompletionStage(
      client
        .querySingle(cls, query)
    )

  }

  def query[A](cls: Class[A], query: String): Task[List[A]] = {

    ZIO
      .fromCompletionStage(
        client
          .query(cls, query)
      )
      .map(_.asScala.toList)

  }
}

object EdgeDbDriver {
  val layer: ULayer[EdgeDbDriverLive] = ZLayer.succeed(EdgeDbDriverLive())
}
