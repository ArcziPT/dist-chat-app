package com.arczipt.chatService.controllers

import play.api.mvc._
import com.google.inject.Inject
import play.api.libs.json.Writes
import play.api.libs.json.JsPath
import play.api.db.slick.DatabaseConfigProvider
import play.api.db.slick.HasDatabaseConfigProvider
import slick.jdbc.JdbcProfile
import scala.concurrent.ExecutionContext
import com.arczipt.chatService.dao.UserDAO
import com.arczipt.chatService.models.User._
import com.arczipt.chatService.dao.ServerDAO
import com.arczipt.chatService.models.Server._
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import akka.compat.Future
import scala.concurrent.Future
import java.time.Clock
import play.api.libs.json.Json
import pdi.jwt.JwtAlgorithm
import play.api.Configuration
import pdi.jwt.JwtJson
import com.arczipt.chatService.system.JWT
import com.arczipt.chatService.auth.Auth
import com.arczipt.chatService.models.ServerMember._
import com.arczipt.chatService.dto.MessageDTO

class ChannelController @Inject() (serverDAO: ServerDAO, jwt: JWT, cc: ControllerComponents)
        (implicit ec: ExecutionContext)
        extends AbstractController(cc){

    def getMessages(channelId: Long, timestamp: Long, number: Integer) = Action.async {request =>
        val id = Auth.getSenderId(jwt, request)
        if(id.isEmpty) Future{Status(403)("error")}

        for{
            channel <- serverDAO.getChannel(channelId)
            members <- {
                if(channel.isDefined) serverDAO.getMembers(channel.get.serverId).map(Some(_))
                else Future{None}
            }
            ret <- {
                if(members.isDefined && members.get.map(_.userId).contains(id.get)){
                    serverDAO.getMessages(channelId, timestamp, number).map{messages => Ok(Json.toJson(messages.map{
                        case (username, message) => MessageDTO(username, message.userId, message.channelId, message.timestamp, message.text)
                    }))}
                }
                else
                    Future{Status(403)("error")}
            }
        } yield ret
    }
}