package me.slivkamiro

import cats.effect.IO
import me.slivkamiro.page.Msg
import tyrian.Html.*
import tyrian.*
import tyrian.cmds.Logger

import scala.scalajs.js.annotation.*

@JSExportTopLevel("TyrianApp")
object TyrianCourse extends TyrianApp[page.Msg, page.Model] {

  override def router: Location => Msg = {
    case loc: Location.Internal =>
      loc.pathName match {
        case "/"          => page.Topics.Msg.Load
        case Topic(topic) => page.Topics.Msg.ViewTopic(topic)
        case _            => page.NoopMsg
      }
    case loc: Location.External => page.NoopMsg
  }

  object Topic {
    def unapply(s: String): Option[String] =
      if (s.startsWith("/") && s.count(_ == '/') == 1 && s.length() > 1)
        Some(s.drop(1))
      else None
  }

  def init(flags: Map[String, String]): (page.Model, Cmd[IO, page.Msg]) =
    (page.Model.Topics(Set()), Backend.topics)

  def update(model: page.Model): page.Msg => (page.Model, Cmd[IO, page.Msg]) =
    case page.Topics.Msg.Load => (model, Backend.topics)
    case page.Topics.Msg.Topics(topics) =>
      (page.Model.Topics(topics), Nav.pushUrl("/"))
    case page.Topics.Msg.TopicsRetrievalError =>
      (model, Logger.error("Failed to retreive Kafka Topics"))
    case page.Topics.Msg.ViewTopic(topic) => (model, Backend.messages(topic))
    case page.TopicMessages.Msg.Messages(topic, messages) =>
      (page.Model.TopicMessages(topic, messages), Nav.pushUrl(s"/$topic"))
    case page.TopicMessages.Msg.MessagesRetrievalError =>
      (model, Logger.error("Failed to retreive Messages from Kafka topic"))
    case page.NoopMsg => (model, Cmd.None)

  def view(model: page.Model): Html[page.Msg] = model match {
    case page.Model.Topics(topics) => page.Topics.view(topics)

    case page.Model.TopicMessages(topic, messages) =>
      page.TopicMessages.view(topic, messages)
  }

  def subscriptions(model: page.Model): Sub[IO, page.Msg] =
    Sub.None
}
