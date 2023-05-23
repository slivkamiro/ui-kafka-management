package me.slivkamiro.page

enum Model:
  case Topics(names: Set[String])
  case TopicMessages(topic: String, messages: List[String])

object NoopMsg

type Msg = Topics.Msg | TopicMessages.Msg | NoopMsg.type
