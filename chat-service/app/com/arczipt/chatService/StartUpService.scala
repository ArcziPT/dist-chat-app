package com.arczipt.chatService

import com.google.inject.Inject
import com.arczipt.chatService.models.UsersTable
import slick.jdbc.PostgresProfile.api._
import play.api.db.slick.HasDatabaseConfigProvider
import slick.jdbc.PostgresProfile
import play.api.db.slick.DatabaseConfigProvider
import scala.concurrent.ExecutionContext
import com.google.inject.Singleton

@Singleton
class StartUpService @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)
        (implicit ec: ExecutionContext)
        extends HasDatabaseConfigProvider[PostgresProfile]{

    println("START")
}