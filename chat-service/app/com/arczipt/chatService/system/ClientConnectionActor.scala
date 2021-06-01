package com.arczipt.chatService.system

import akka.actor._
import com.google.inject.Inject
import akka.cluster.pubsub.DistributedPubSub
import akka.cluster.pubsub.DistributedPubSubMediator._
import play.api.libs.json.JsValue
import pdi.jwt.JwtAlgorithm
import pdi.jwt.JwtJson
import play.api.libs.json.Json
import play.api.libs.json.Writes
import com.arczipt.chatService.models.User._
import com.arczipt.chatService.dao.UserDAO
import com.arczipt.chatService.dao.ServerDAO
import scala.concurrent.ExecutionContext
import com.arczipt.chatService.models.Server
import play.api.libs.json.JsSuccess
import org.lmdbjava.Stat

object ClientConnectionActor {
    def props(client: ActorRef, jwt: JWT, userDAO: UserDAO, serverDAO: ServerDAO)
        (implicit system: ActorSystem, ec: ExecutionContext) = 
        Props(new ClientConnectionActor(client, jwt, userDAO, serverDAO))

    //authMsg
    case class AuthMsg(token: String, serverId: Long)
    
    case class Status(status: String)

    //received from user
    case class SendMessage(timestamp: Long, 
                           text: String,
                           channelId: Long)
    //message published to topic and send to user
    case class ReceiveMessage(username: String,
                              userId: Long,
                              channelId: Long,
                              timestamp: Long, 
                              text: String)

    case class UserData(user: User)
    case class ChannelsData(channels: Seq[Server.Channel])

    case class ChangeServer(serverId: Long)

    //JSON
    implicit val sendMessageReads = Json.reads[SendMessage]
    implicit val authMsgReads = Json.reads[AuthMsg]

    implicit val receiveMessageWrites = Json.writes[ReceiveMessage]
    implicit val statusWrites = Json.writes[Status]
    
    implicit val changeRead = Json.reads[ChangeServer]
}

class ClientConnectionActor 
        (val client: ActorRef, jwt: JWT, userDAO: UserDAO, serverDAO: ServerDAO)
        (implicit system: ActorSystem, ec: ExecutionContext) extends Actor with ActorLogging {
    import ClientConnectionActor._

    //PubSub mediator
    val mediator = DistributedPubSub(system).mediator

    val msgActor = system.actorOf(Props(new MessagePersistanceActor(serverDAO)))

    //client's and session's data
    var user: User = null
    var serverId: Long = 0
    var channels: Seq[Server.Channel] = null
    var topic: String = ""

    //at the begining authorize user
    def receive = authorize

    def authorize: Receive = {
        case msg: JsValue => {
            val authMsg = msg.as[AuthMsg]
            val claim = jwt.decode(authMsg.token)

            if(claim.isSuccess) {
                //next step is setup
                context.become(setup)

                //retrieve user's data
                getUser((claim.get \ "username").as[String])
                //retrieve server's data
                getServer(serverId)

                //update session's serverId
                this.serverId = serverId
            }else {
                client ! Json.toJson(Status("ill formed token"))
                self ! PoisonPill
            }
        }
    }

    def normal: Receive = {
        case msg: JsValue => {
            val _type = (msg \ "type").as[String]
            val _json = (msg \ "msg").as[JsValue]

            _type match {
                case "send" => {
                    val sendMessage = _json.as[SendMessage]
                    mediator ! Publish(
                        topic, 
                        ReceiveMessage(user.username, user.id.get, sendMessage.channelId, sendMessage.timestamp, sendMessage.text)
                    )
                    msgActor ! MessagePersistanceActor.Persist(user.id.get, sendMessage.timestamp, sendMessage.channelId, sendMessage.text)
                }

                case "change" => {
                    val changeServer = _json.as[ChangeServer]
                    mediator ! Unsubscribe(topic, self)
                    serverId = 0
                    channels = Seq.empty[Server.Channel]
                    getServer(changeServer.serverId)
                    context.become(setup)
                }
            }
        }
        case msg: ReceiveMessage => client ! Json.toJson(msg)
    }

    def setup: Receive = {
        //wait for user data
        case UserData(user) => {
            this.user = user
            checkReady
        }

        //wait for server data
        case ChannelsData(channels) => {
            this.channels = channels
            checkReady
        }

        //wait for subscription ack
        case SubscribeAck(s) => {
            context.become(normal)
            topic = s"server-${serverId}"
            client ! Json.toJson(Status("ready"))
        }

        //ignore rest
        case other => ()
    }

    def checkReady = if(user != null && channels != null) mediator ! Subscribe(s"server-${serverId}", self)

    def getServer(serverId: Long) = {
        serverDAO.getChannels(serverId).foreach(channels => self ! ChannelsData(channels))
    }

    def getUser(username: String) = {
        userDAO.getByUsername(username).foreach(opt => self ! UserData(opt.get))
    }

    override def postStop(): Unit = {
        if(topic != null) mediator ! Unsubscribe(topic, self)
    }
}