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
import com.arczipt.chatService.dao.ServerDAO
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
import com.arczipt.chatService.models.User._
import com.arczipt.chatService.auth.Auth
import com.arczipt.chatService.dto.com.arczipt.chatService.dto.UserServer
import com.arczipt.chatService.dto.com.arczipt.chatService.dto.ServerChannel

class UserController @Inject() (userDAO: UserDAO, jwt: JWT, cc: ControllerComponents)
        (implicit ec: ExecutionContext)
        extends AbstractController(cc){

    def getUsers = Action.async {request =>
        userDAO.all().map {users => Ok(Json.toJson(users.map(user => new UserDTO(user.id.get, user.username))))}
    }

    def addUser = Action.async(parse.json) {request =>
        val json = request.body
        val username = (json \ "username").as[String]
        val password = (json \ "password").as[String]

        userDAO.insert(User(None, username, password)).map(user => Ok(Json.toJson(new UserDTO(user.id.get, user.username))))
    }

    def getChannels = Action.async {request =>
        val id = Auth.getSenderId(jwt, request)
        if(id.isEmpty) Future{Status(403)("error")}

        userDAO.getChannels(id.get).map{ channels =>
            Ok(Json.toJson(
                channels.groupBy{
                    case (role, serverId, serverName, channelId, channelName) => (serverId, serverName)
                }.mapValues{channels => channels.map{
                    case (role, serverId, serverName, channelId, channelName) => ServerChannel(role, serverId, channelId, channelName)
                }}.collect{
                    case ((serverId, serverName), channels) => UserServer(serverId, serverName, channels)
                }
            ))
        }
    }
}