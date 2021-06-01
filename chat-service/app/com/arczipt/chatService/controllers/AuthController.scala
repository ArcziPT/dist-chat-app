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

class AuthController @Inject() (userDAO: UserDAO, jwt: JWT, cc: ControllerComponents)
        (implicit ec: ExecutionContext)
        extends AbstractController(cc){

    def auth = Action.async(parse.json) {request =>
        val json = request.body
        val username = (json \ "username").as[String]
        val password = (json \ "password").as[String]

        userDAO.getByUsername(username).map {user =>
            if(user.isDefined && user.get.password == password) {
                val claim = Json.obj("username" -> username, "userId" -> user.get.id)
                val token = jwt.encode(claim)
                Ok(claim).withHeaders("Authorization" -> s"bearer ${token}")
            }
            else Status(403)("Wrong username or password")
        }
    }
}