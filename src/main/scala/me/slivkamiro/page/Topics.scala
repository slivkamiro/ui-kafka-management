package me.slivkamiro.page

import io.circe.parser
import tyrian.Html.*
import tyrian.*
import tyrian.http.*

object Topics:

  enum Msg:
    case Load
    case Topics(value: Set[String])
    case ViewTopic(topic: String)
    case TopicsRetrievalError

  object Msg:
    val decoder = Decoder(
      resp =>
        parser
          .decode[List[String]](resp.body)
          .fold(_ => Msg.TopicsRetrievalError, v => Msg.Topics(v.toSet)),
      err => Msg.TopicsRetrievalError
    )

  def view(topics: Set[String]) = div(
    ul(
      topics
        .map(topic =>
          li(
            div(
              div(topic),
              div(
                button(onClick(Msg.ViewTopic(topic)))(
                  "view"
                )
              )
            )
          )
        )
        .toSeq: _*
    )
  )
