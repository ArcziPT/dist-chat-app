package com.arczipt.chatService.controllers

import play.api.mvc._
import play.api.libs.streams.ActorFlow
import javax.inject.Inject
import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.cluster.Cluster
import com.arczipt.chatService.system.ClientConnectionActor
import akka.cluster.MemberStatus
import akka.cluster.pubsub.DistributedPubSub
import akka.actor.typed.eventstream.EventStream._
import akka.cluster.ClusterEvent._
import akka.cluster.ClusterEvent
import play.api.libs.json._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext
import play.api.Configuration
import pdi.jwt.JwtAlgorithm
import pdi.jwt.JwtJson
import com.arczipt.chatService.dao.UserDAO
import com.arczipt.chatService.dao.ServerDAO
import com.arczipt.chatService.system.JWT

class ChatConnectionController @Inject() 
        (userDAO: UserDAO, serverDAO: ServerDAO, jwt: JWT, cc: ControllerComponents)
        (implicit system: ActorSystem, implicit val ec: ExecutionContext, mat: Materializer)
        extends AbstractController(cc) {

    def socket() = WebSocket.accept[JsValue, JsValue] { request =>
        ActorFlow.actorRef {client => ClientConnectionActor.props(client, jwt, userDAO, serverDAO)}
    }
}