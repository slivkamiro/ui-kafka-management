package me.slivkamiro

import com.comcast.ip4s._
import sttp.tapir.*
import sttp.tapir.json.circe._
import sttp.tapir.server.http4s.Http4sServerInterpreter
import cats.effect.*
import cats.syntax.either.*
import fs2.kafka.*
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.middleware.CORS
import org.http4s.server.middleware.CORSConfig

object Main extends IOApp {

  val listTopics = endpoint
    .in("topics")
    .out(jsonBody[List[String]])


  def endpoints(admin: KafkaAdminClient[IO]) = List(
    listTopics.serverLogic(_ => admin.listTopics.names.map(_.toList.asRight))
  )



  override def run(args: List[String]): IO[ExitCode] = KafkaAdminClient
    .resource[IO](AdminClientSettings("localhost:9092"))
    .flatMap { admin =>

      val routes = Http4sServerInterpreter[IO]().toRoutes(endpoints(admin))

      EmberServerBuilder
        .default[IO]
        .withHost(ipv4"0.0.0.0")
        .withPort(port"80")
        .withHttpApp(CORS.policy.withAllowOriginAll(routes.orNotFound))
        .build
    }
    .use(_ => IO.never.as(ExitCode.Success))
}
