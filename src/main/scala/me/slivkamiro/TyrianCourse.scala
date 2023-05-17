package me.slivkamiro

import cats.effect.IO
import tyrian.Html.*
import tyrian.*
import tyrian.cmds.Logger

import scala.scalajs.js.annotation.*

@JSExportTopLevel("TyrianApp")
object TyrianCourse extends TyrianApp[page.Msg, page.Model] {

  def init(flags: Map[String, String]): (page.Model, Cmd[IO, page.Msg]) =
    (page.Model.Topics(Set()), Backend.topics)

  def update(model: page.Model): page.Msg => (page.Model, Cmd[IO, page.Msg]) =
    case page.Topics.Msg.Topics(topics) => (page.Model.Topics(topics), Cmd.None)
    case page.Topics.Msg.TopicsRetrievalError =>
      (model, Logger.error("Failed to retreive Kafka Topics"))
    case page.Topics.Msg.ViewTopic(topic) => (model, Backend.messages(topic))
    case page.TopicMessages.Msg.Messages(topic, messages) =>
      (page.Model.TopicMessages(topic, messages), Cmd.None)
    case page.TopicMessages.Msg.MessagesRetrievalError =>
      (model, Logger.error("Failed to retreive Messages from Kafka topic"))

  def view(model: page.Model): Html[page.Msg] = model match {
    case page.Model.Topics(topics) => page.Topics.view(topics)

    case page.Model.TopicMessages(topic, messages) =>
      page.TopicMessages.view(topic, messages)
  }

  def subscriptions(model: page.Model): Sub[IO, page.Msg] =
    Sub.None
}
