package com.arczipt.chatService.models
import slick.jdbc.MySQLProfile.api._
import com.arczipt.chatService.models.User._

object Server{
    case class Server(id: Option[Long],
                      name: String)
    class ServersTable(tag: Tag) extends Table[Server](tag, "Servers") {
        def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
        def name = column[String]("name", O.Unique, O.Length(20))

        def * = (id.?, name) <> (Server.tupled, Server.unapply)
    }
    val servers = TableQuery[ServersTable]


    case class Channel(id: Option[Long],
                       serverId: Long,
                       name: String)
    class ChannelsTable(tag: Tag) extends Table[Channel](tag, "Channels") {
        def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
        def serverId = column[Long]("serverId")
        def name = column[String]("name", O.Length(20))

        def uniqe_server_channel = index("server_channel", (serverId, name), true)
        def server = foreignKey("server_fk", serverId, servers)(_.id, onUpdate=ForeignKeyAction.Restrict, onDelete=ForeignKeyAction.Cascade)

        def * = (id.?, serverId, name) <> (Channel.tupled, Channel.unapply)
    }
    val channels = TableQuery[ChannelsTable]

    
    case class Message(userId: Long,
                       timestamp: Long,
                       channelId: Long,
                       text: String)
    class MessagesTable(tag: Tag) extends Table[Message](tag, "Messages") {
        def userId = column[Long]("userId")
        def timestamp = column[Long]("timestamp")
        def channelId = column[Long]("channelId")
        def text = column[String]("text")

        def channel = foreignKey("message_channel_fk", channelId, channels)(_.id, onUpdate=ForeignKeyAction.Restrict, onDelete=ForeignKeyAction.Cascade)
        def user = foreignKey("message_user_fk", userId, users)(_.id, onUpdate=ForeignKeyAction.Restrict, onDelete=ForeignKeyAction.Cascade)

        def * = (userId, timestamp, channelId, text) <> (Message.tupled, Message.unapply)
    }
    val messages = TableQuery[MessagesTable]
}