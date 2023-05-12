package me.slivkamiro

import cats.effect.IO
import io.circe.parser
import tyrian.Html.*
import tyrian.*
import tyrian.cmds.Logger
import tyrian.http.*

import scala.scalajs.js.annotation.*

@JSExportTopLevel("TyrianApp")
object TyrianCourse extends TyrianApp[Msg, Model] {

  def init(flags: Map[String, String]): (Model, Cmd[IO, Msg]) =
    (Set(), Http.send(Request.get("http://localhost:80/topics").withHeaders(Header("Access-Control-Allow-Origin","*")), Decoder(resp => parser.decode[List[String]](resp.body).fold(_ => Msg.TopicsRetrievalError, v => Msg.Topics(v.toSet)), err => Msg.TopicsRetrievalError)))

  def update(model: Model): Msg => (Model, Cmd[IO, Msg]) =
    case Msg.Topics(topics) => (topics, Cmd.None)
    case Msg.TopicsRetrievalError => (Set.empty, Logger.error("Failed to retreive Kafka Topics"))

  def view(model: Model): Html[Msg] =
    div(
      ul(model.map(li(_)).toSeq:_*)
    )

  def subscriptions(model: Model): Sub[IO, Msg] =
    Sub.None
}

type Model = Set[String]

enum Msg:
  case Topics(value: Set[String])
  case TopicsRetrievalError
