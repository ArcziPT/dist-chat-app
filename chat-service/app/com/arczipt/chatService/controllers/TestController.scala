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

class TestController @Inject() (userDAO: UserDAO, serverDAO: ServerDAO, jwt: JWT, cc: ControllerComponents)
        (implicit ec: ExecutionContext)
        extends AbstractController(cc){

    def auth = Action.async(parse.json) {request =>
        val json = request.body
        val username = (json \ "username").as[String]
        val password = (json \ "password").as[String]

        userDAO.getByUsername(username).map {user =>
            if(user.isDefined && user.get.password == password) {
                val claim = Json.obj("username" -> username)
                val token = jwt.encode(claim)
                Ok("ok").withHeaders("Authorization" -> s"bearer ${token}")
            }
            else Status(403)("Wrong username or password")
        }
    }

    def addChannel() = Action.async {request =>
        serverDAO.addChannel(1, "main").map(_ => Ok("ok"))
    }

    def getUsers = Action.async {request =>
        userDAO.all().map {case users => Ok(users.map(user => user.username).reduce((x, y) => x + y))}
    }

    def addUser = Action.async(parse.json) {request =>
        val json = request.body
        val username = (json \ "username").as[String]
        val password = (json \ "password").as[String]

        userDAO.insert(User(None, username, password)).map(id => Ok(id.toString))
    }

    def getServers = Action.async {request =>
        serverDAO.all().map {case servers => Ok(servers.map(server => server.name).reduce((x, y) => x + y))}
    }

    def create = Action.async {request => 
        Await.result(userDAO.create, Duration.Inf)
        Await.result(serverDAO.createServers, Duration.Inf)
        Await.result(serverDAO.createServerMembers, Duration.Inf)
        Await.result(serverDAO.createChannels, Duration.Inf)
        Await.result(serverDAO.createMessages, Duration.Inf)

        val user1_id = Await.result(userDAO.insert(User(None, "user1", "pass1")), Duration.Inf)
        val user2_id = Await.result(userDAO.insert(User(None, "user2", "pass2")), Duration.Inf)

        val server_id = Await.result(serverDAO.insert(Server(None, "server1"), user1_id), Duration.Inf)

        Await.result(serverDAO.addMember(user2_id, server_id), Duration.Inf)

        Future{Ok("dziala")}
    }
}