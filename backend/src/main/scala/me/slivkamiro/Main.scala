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
import scala.concurrent.duration.*
import sttp.tapir.server.ServerEndpoint

object Main extends IOApp {

  val listTopics = endpoint
    .in("topics")
    .out(jsonBody[List[String]])

  val topicMessages = endpoint
    .in("topics" / path[String]("topic"))
    .in(query[Int]("size").default(10))
    .out(jsonBody[List[String]])

  def endpoints(admin: KafkaAdminClient[IO]): List[ServerEndpoint[Any, IO]] = List(
    listTopics.serverLogic(_ => admin.listTopics.names.map(_.toList.asRight)),
    topicMessages.serverLogic { (topic, size) =>
      KafkaConsumer[IO]
        .stream(
          ConsumerSettings[IO, Array[Byte], String].withBootstrapServers(
            "localhost:9092"
          )
        )
        .evalTap(_.assign(topic))
        .evalTap(_.seekToBeginning)
        .records
        .take(size)
        .map(_.record.value)
        .interruptAfter(1.second)
        .compile
        .toList
        .map(_.asRight)
    }
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
