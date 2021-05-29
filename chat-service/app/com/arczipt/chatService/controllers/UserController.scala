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
import com.arczipt.chatService.models.User

class UserController @Inject() (userDAO: UserDAO, cc: ControllerComponents)
        (implicit ec: ExecutionContext)
        extends AbstractController(cc){

    def getTest = Action.async {request =>
        userDAO.all().map {case users => Ok(users.map(user => user.username).reduce((x, y) => x + y))}
    }

    def addTest = Action.async {request =>
        userDAO.insert("user123", "pass123").map(_ => Ok("inserted"))
    }

    def create = Action.async {request =>
        userDAO.create.map(_ => Ok("dziala"))
    }
}