package com.arczipt.chatService.system

import com.arczipt.chatService.dao.UserDAO
import com.arczipt.chatService.dao.ServerDAO
import akka.actor.ActorSystem
import scala.concurrent.ExecutionContext
import akka.actor.Actor
import com.arczipt.chatService.models.Server

object MessagePersistanceActor{
    case class Persist(userId: Long, timestampe: Long, channelId: Long, text: String)
    case class PersistAck(id: Long)
}

class MessagePersistanceActor(serverDAO: ServerDAO)
        (implicit system: ActorSystem, ec: ExecutionContext) extends Actor{
    
    import MessagePersistanceActor._ 

    var _id: Long = 0
    def nextId = {
        val ret = _id
        _id = _id + 1
        ret
    }

    var msgWaitingForAck = Map.empty[Long, Server.Message]
        
    def receive: Receive = {
        case Persist(userId, timestamp, channelId, text) => {
            val id = nextId

            val msg = Server.Message(userId, timestamp, channelId, text)
            msgWaitingForAck += (id -> msg)
            serverDAO.addMessage(msg) onComplete {_ => self ! PersistAck(id)}
        }
        case PersistAck(id) => {
            msgWaitingForAck -= id
        }
    }
}