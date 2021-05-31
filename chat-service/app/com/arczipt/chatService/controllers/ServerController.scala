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

class ServerController @Inject() (userDAO: UserDAO, serverDAO: ServerDAO, jwt: JWT, cc: ControllerComponents)
        (implicit ec: ExecutionContext)
        extends AbstractController(cc){

    def getMembers(serverId: Long) = Action.async {request =>
        val id = Auth.getSenderId(jwt, request)
        if(id.isEmpty) Future{Status(403)("error")}

        for{
            verified <- serverDAO.getMembers(serverId)
            members <- userDAO.all().map(users => Ok(Json.toJson(users.map(user => new UserDTO(user.id.get, user.username)))))
        } yield members
    }

    def getServers = Action.async {request =>
        serverDAO.all().map {servers => Ok(Json.toJson(servers))}
    }
}