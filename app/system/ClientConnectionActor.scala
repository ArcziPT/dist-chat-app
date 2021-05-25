package system

import akka.actor._

object ClientConnectionActor {
  def props(client: ActorRef) = Props(new ClientConnectionActor(client))
}

class ClientConnectionActor(val client: ActorRef) extends Actor {
  def receive = {
    case msg: String =>
      client ! ("I received your message: " + msg)
  }
}