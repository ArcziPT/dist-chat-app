package com.arczipt.chatService.models
import slick.jdbc.MySQLProfile.api._
import com.arczipt.chatService.models.User._
import com.arczipt.chatService.models.Server._

object ServerMember{
    case class ServerMember(serverId: Long,
                            userId: Long,
                            role: String)
    class ServerMembersTable(tag: Tag) extends Table[ServerMember](tag, "ServerMember") {
        def serverId = column[Long]("serverId")
        def userId = column[Long]("userId")
        def role = column[String]("role", O.Length(20))

        def member_primary_key = primaryKey("pk", (serverId, userId))
        def server = foreignKey("channel_fk", serverId, servers)(_.id, onUpdate=ForeignKeyAction.Restrict, onDelete=ForeignKeyAction.Cascade)
        def user = foreignKey("user_fk", userId, users)(_.id, onUpdate=ForeignKeyAction.Restrict, onDelete=ForeignKeyAction.Cascade)

        def * = (serverId, userId, role) <> (ServerMember.tupled, ServerMember.unapply)
    }
    val serverMembers = TableQuery[ServerMembersTable]
}