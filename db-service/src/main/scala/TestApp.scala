package com.arczipt.dbService

import akka.actor.ActorSystem
import akka.actor.AbstractActor
import akka.actor.Actor
import akka.actor.Props
import akka.cluster.Cluster
import akka.cluster.pubsub.DistributedPubSub
import akka.actor.typed.eventstream.EventStream
import akka.cluster.pubsub.DistributedPubSubMediator.{Subscribe, Publish}

class TestActor(implicit system: ActorSystem) extends Actor{
  val mediator = DistributedPubSub(system).mediator
  mediator ! Publish("content", "dbNode")
  mediator ! Subscribe("content", self)

  def receive = {
    case text: String => println(text)
  }
}

object TestApp extends App{
  implicit val system = ActorSystem("ChatCluster")
  val cluster = Cluster(system)

  system.actorOf(Props(new TestActor()))
}