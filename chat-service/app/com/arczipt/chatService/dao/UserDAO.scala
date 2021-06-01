package com.arczipt.chatService.dao

import com.google.inject.Inject
import play.api.db.slick.DatabaseConfigProvider
import scala.concurrent.ExecutionContext
import play.api.db.slick.HasDatabaseConfigProvider
import scala.concurrent.Future
import slick.jdbc.MySQLProfile
import com.arczipt.chatService.models.User._
import com.arczipt.chatService.models.ServerMember._
import com.arczipt.chatService.models.Server._

class UserDAO @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) extends HasDatabaseConfigProvider[MySQLProfile] {
    import slick.jdbc.MySQLProfile.api._

    def all(): Future[Seq[User]] = db.run(users.result)

    def insert(user: User): Future[User] = 
        db.run(users returning users += user)

    def getByUsername(username: String) = {
        val q = for{ 
            user <- users if user.username === username
        } yield(user)
        db.run(q.result.headOption)
    }

    def getById(id: Long) = {
        val q = for{ user <- users if user.id === id } yield(user)
        db.run(q.result.headOption)
    }

    def getChannels(userId: Long): Future[Seq[(String, Long, String, Long, String)]] = {
        val q = serverMembers filter(_.userId === userId) join 
            servers on(_.serverId === _.id) join
            channels on{
                case ((serverMember, server), channel) => {
                    server.id === channel.serverId
                }
            } map {
                case ((serverMember, server), channel) => {
                    (serverMember.role, server.id, server.name, channel.id, channel.name)
                }
            } sortBy{
                case (role, serverId, serverName, channelId, channelName) => serverId
            }
        db.run(q.result)
    }

    def create = {
        val users = TableQuery[UsersTable]
        val result = db.run(users.schema.create)
        result.map(r => println("Users Table Created"))
    }

    def drop = {
        val users = TableQuery[UsersTable]
        val result = db.run(users.schema.drop)
        result.map(r => println("Users Table Dropped"))
    }
}