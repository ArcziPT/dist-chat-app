package com.arczipt.chatService.dto

import play.api.libs.json.Json

object MessageDTO{
    implicit val messageWrite = Json.writes[MessageDTO]
}

case class MessageDTO(username: String, userId: Long, channelId: Long, timestamp: Long, text: String)