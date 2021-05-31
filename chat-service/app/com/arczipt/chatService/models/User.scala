package com.arczipt.chatService.models
import slick.jdbc.MySQLProfile.api._
import play.api.libs.json.Json

object User{
    case class User(id: Option[Long], 
                    username: String, 
                    password: String)

    class UsersTable(tag: Tag) extends Table[User](tag, "Users") {
        def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
        def username = column[String]("username", O.Unique, O.Length(20))
        def password = column[String]("password", O.Length(20))

        def * = (id.?, username, password) <> (User.tupled, User.unapply)
    }
    val users = TableQuery[UsersTable]

    case class UserDTO(id: Long, username: String)
    implicit val userDTOWrite = Json.writes[UserDTO]
}