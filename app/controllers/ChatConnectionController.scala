package controllers

import play.api.mvc._
import play.api.libs.streams.ActorFlow
import javax.inject.Inject
import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.cluster.Cluster
import system.ClientConnectionActor
import akka.cluster.MemberStatus

class ChatConnectionController @Inject() (cc: ControllerComponents)(implicit system: ActorSystem, mat: Materializer)
    extends AbstractController(cc) {

  val cluster = Cluster(system)

  def socket() = WebSocket.accept[String, String] { request =>
    ActorFlow.actorRef { client =>
      ClientConnectionActor.props(client)
    }
  }

  def members() = Action {request =>
    Ok(cluster.state.members
      .filter(member => member.status ==  MemberStatus.up).unsorted
      .map(member => member.address.toString)
      .toString())
  }
}