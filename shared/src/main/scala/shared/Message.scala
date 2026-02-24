package shared

// Shared data model used by both frontend and backend
case class Message(text: String, timestamp: Long)

object Message:
  def create(text: String): Message =
    Message(text, System.currentTimeMillis())

  def hello: Message =
    create("Hello from Scala Full-Stack!")
