package me.slivkamiro.page

import io.circe.parser
import tyrian.Html.*
import tyrian.*
import tyrian.http.*

object TopicMessages:

  enum Msg:
    case Messages(topic: String, value: List[String])
    case MessagesRetrievalError

  object Msg:
    def decoder(topic: String) = Decoder(
      resp =>
        parser
          .decode[List[String]](resp.body)
          .fold(_ => Msg.MessagesRetrievalError, v => Msg.Messages(topic, v)),
      err => Msg.MessagesRetrievalError
    )

  def view(topic: String, messages: List[String]) = div(
    div(s"messages of $topic"),
    ul(
      messages.map(msg => li(text(msg)))
    )
  )
