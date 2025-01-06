package services

import zio._
import com.edgedb.driver.EdgeDBClient;
import models.Trip
import scala.jdk.CollectionConverters._
import java.util.concurrent.CompletionStage
import scala.jdk.FutureConverters.*
case class EdgeDbDriverLive() {
  private var client = new EdgeDBClient();

  def querySingle[A](
      cls: Class[A],
      query: String
  ): Task[java.util.List[A]] = {
    // var toto = client
    //   .querySingle(cls, query)

    var toto2 = client
      .query(cls, query.stripMargin)
      .asScala
    var toto3 = ZIO.fromFuture(_ => toto2)
    toto3

  }

  // def query[A](cls: Class[A], query: String): Task[List[A]] = {

  //   ZIO
  //     .fromCompletionStage(
  //       client
  //         .query(cls, query)
  //     )
  //     .map(_.asScala.toList)

  // }
}

object EdgeDbDriver {
  val layer: ULayer[EdgeDbDriverLive] = ZLayer.succeed(EdgeDbDriverLive())
}
