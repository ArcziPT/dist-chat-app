package com.arczipt.dbService

trait SerializableMessage

object Message{
    case class Simple(text: String) extends SerializableMessage
}