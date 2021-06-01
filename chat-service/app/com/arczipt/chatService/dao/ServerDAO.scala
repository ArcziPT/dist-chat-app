package com.arczipt.chatService.dao

import com.google.inject.Inject
import play.api.db.slick.DatabaseConfigProvider
import scala.concurrent.ExecutionContext
import play.api.db.slick.HasDatabaseConfigProvider
import scala.concurrent.Future
import slick.jdbc.MySQLProfile
import com.arczipt.chatService.models.Server._
import com.arczipt.chatService.models.ServerMember._
import com.arczipt.chatService.models.User._
import scala.util.Success
import scala.util.Failure

class ServerDAO @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) extends HasDatabaseConfigProvider[MySQLProfile] {
    import slick.jdbc.MySQLProfile.api._

    def all(): Future[Seq[Server]] = db.run(servers.result)

    def insert(server: Server, ownerId: Long): Future[Long] = {
        val q = for{
            serverId <- servers returning servers.map(_.id) += server
            member <- serverMembers += ServerMember(serverId, ownerId, "ADMIN")
            channel <- channels += Channel(None, serverId, "main")
        } yield serverId
        
        db.run(q)
    }

    def getByName(name: String) = {
        val q = for{ 
            server <- servers if server.name === name
        } yield(server)
        db.run(q.result.headOption)
    }

    def getById(id: Long) = {
        val q = for{ server <- servers if server.id === id } yield(server)
        db.run(q.result.headOption)
    }

    def getMembers(serverId: Long): Future[Seq[ServerMember]] = {
        val q = serverMembers filter(_.serverId === serverId)
        db.run(q.result)
    }

    def addMember(userId: Long, serverId: Long): Future[Unit] = {
        val q = serverMembers += ServerMember(serverId, userId, "NONE")
        db.run(q).map(_ => ())
    }

    def getChannels(serverId: Long): Future[Seq[Channel]] = {
        val q = channels filter(_.serverId === serverId)
        db.run(q.result)
    }

    def addChannel(serverId: Long, name: String): Future[Long] = {
        val q = channels returning channels.map(_.id) += Channel(None, serverId, name)
        db.run(q)
    }

    def getChannel(channelId: Long): Future[Option[Channel]] = {
        val q = channels filter(_.id === channelId)
        db.run(q.result.headOption)
    }

    def getMessages(channelId: Long, timestamp: Long, number: Integer): Future[Seq[(String, Message)]] = {
        val q = messages.
            filter(_.channelId === channelId).
            filter(m => m.timestamp <= timestamp).
            sortBy(_.timestamp.desc).
            take(number) join users on((message: MessagesTable, user: UsersTable) => message.userId === user.id) map{
                case (message, user) => (user.username, message)
            }

        db.run(q.result)
    }

    def addMessage(message: Message): Future[Unit] = {
        val q = messages += message
        db.run(q).map(_ => ())
    }

    def createServers = {
        val servers = TableQuery[ServersTable]
        val result = db.run(servers.schema.create)
        result.map(r => println("Servers Table Created"))
    }

    def createMessages = {
        val servers = TableQuery[MessagesTable]
        val result = db.run(servers.schema.create)
        result.map(r => println("Messages Table Created"))
    }

    def createChannels = {
        val servers = TableQuery[ChannelsTable]
        val result = db.run(servers.schema.create)
        result.map(r => println("Channels Table Created"))
    }

    def createServerMembers = {
        val servers = TableQuery[ServerMembersTable]
        val result = db.run(servers.schema.create)
        result.map(r => println("ServerMembers Table Created"))
    }

    def dropServers = {
        val servers = TableQuery[ServersTable]
        val result = db.run(servers.schema.drop)
        result.map(r => println("Servers Table Dropped"))
    }

    def dropMessages = {
        val servers = TableQuery[MessagesTable]
        val result = db.run(servers.schema.drop)
        result.map(r => println("Messages Table Dropped"))
    }

    def dropChannels = {
        val servers = TableQuery[ChannelsTable]
        val result = db.run(servers.schema.drop)
        result.map(r => println("Channels Table Dropped"))
    }

    def dropServerMembers = {
        val servers = TableQuery[ServerMembersTable]
        val result = db.run(servers.schema.drop)
        result.map(r => println("ServerMembers Table Dropped"))
    }
}