package system

trait SerializableMessage

object Message{
    case class Simple(text: String) extends SerializableMessage
}