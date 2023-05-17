package me.slivkamiro

import cats.effect.*
import tyrian.http.*

object Backend:
  val topics = Http.send[IO, List[String], page.Topics.Msg](Request.get("http://localhost:80/topics"), page.Topics.Msg.decoder)

  def messages(topic: String) = Http.send[IO, List[String], page.TopicMessages.Msg](Request.get(s"http://localhost:80/topics/$topic?size=3"), page.TopicMessages.Msg.decoder(topic))
