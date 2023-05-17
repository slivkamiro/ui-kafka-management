package me.slivkamiro.page

enum Model:
  case Topics(names: Set[String])
  case TopicMessages(topic: String, messages: List[String])

type Msg = Topics.Msg | TopicMessages.Msg
