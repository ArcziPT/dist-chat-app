package com.arczipt.chatService.controllers

import play.api.mvc._
import play.api.libs.streams.ActorFlow
import javax.inject.Inject
import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.cluster.Cluster
import com.arczipt.dbService.system.ClientConnectionActor
import akka.cluster.MemberStatus
import akka.cluster.pubsub.DistributedPubSub
import akka.actor.typed.eventstream.EventStream._
import akka.cluster.ClusterEvent._
import akka.cluster.ClusterEvent

class ChatConnectionController @Inject() (cc: ControllerComponents)(implicit system: ActorSystem, mat: Materializer)
    extends AbstractController(cc) {

  //TODO: only for tests
  val cluster = Cluster(system)

  def socket() = WebSocket.accept[String, String] { request =>
    ActorFlow.actorRef { client =>
      ClientConnectionActor.props(client)
    }
  }

  //TODO: only for test
  def members() = Action {request =>
    Ok(cluster.state.members
      .filter(member => member.status ==  MemberStatus.up)
      .map(member => member.address.toString)
      .toString())
  }
}