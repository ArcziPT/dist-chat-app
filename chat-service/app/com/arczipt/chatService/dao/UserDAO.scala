package com.arczipt.chatService.dao

import com.google.inject.Inject
import play.api.db.slick.DatabaseConfigProvider
import scala.concurrent.ExecutionContext
import play.api.db.slick.HasDatabaseConfigProvider
import scala.concurrent.Future
import com.arczipt.chatService.models.User
import slick.jdbc.MySQLProfile
import com.arczipt.chatService.models.UsersTable

class UserDAO @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) extends HasDatabaseConfigProvider[MySQLProfile] {
    import slick.jdbc.MySQLProfile.api._

    private val Users = TableQuery[UsersTable]

    def all(): Future[Seq[User]] = db.run(Users.result)

    def insert(username: String, password: String): Future[Unit] = 
        db.run(Users returning Users.map(_.id) += User(None, username, password)).map { _ => () }

    def create = {
        val users = TableQuery[UsersTable]
        val result = db.run(users.schema.create)
        result.map(r => println("Stworzony"))
    }
}