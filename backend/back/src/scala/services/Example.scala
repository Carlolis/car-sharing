package services

import com.edgedb.driver.EdgeDBClient

import scala.concurrent.{ExecutionContext, Future}

trait Example {
  def run(client: EdgeDBClient)(implicit context: ExecutionContext): Future[Unit];
}
 