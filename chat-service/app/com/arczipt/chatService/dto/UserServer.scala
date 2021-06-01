package com.arczipt.chatService.dto

package com.arczipt.chatService.dto

import play.api.libs.json.Json

object UserServer{
    implicit val userChannelWrite = Json.writes[ServerChannel]
    implicit val userServerWrite = Json.writes[UserServer]
}

case class UserServer(serverId: Long, name: String, channels: Seq[ServerChannel])
case class ServerChannel(role: String, serverId: Long, channelId: Long, name: String)