package com.arczipt.dbService.system

import akka.actor._
import com.google.inject.Inject
import akka.cluster.pubsub.DistributedPubSub
import akka.cluster.pubsub.DistributedPubSubMediator._

object ClientConnectionActor {
  def props(client: ActorRef)(implicit system: ActorSystem) = Props(new ClientConnectionActor(client))

  case class Message(from: String, text: String) extends Serializable
}

class ClientConnectionActor(val client: ActorRef)(implicit system: ActorSystem) extends Actor with ActorLogging {

  val mediator = DistributedPubSub(system).mediator
  mediator ! Subscribe("content", self)

  def receive = {
    case msg: String => {
      mediator ! Publish("content", ClientConnectionActor.Message(self.path.toString(), msg))
      client ! ("I received your message: " + msg)
    }
    case ClientConnectionActor.Message(from, to) => {
      client ! from + ": " + to
    }
    case SubscribeAck(Subscribe("content", None, `self`)) =>
      log.info("subscribed");
  }
}