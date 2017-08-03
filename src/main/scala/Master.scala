import akka.actor.{Actor, Props, Terminated}
import akka.routing._

import akka.routing.RoundRobinRoutingLogic
import akka.routing.RoutingLogic
import akka.routing.Routee
import akka.routing.SeveralRoutees
import scala.collection.immutable._

class Master extends Actor {

  import Master._

  var router = {
    val routees = Vector.tabulate(5) { a =>
      val r = context.actorOf(Props[Worker], a.toString)
      context watch r
      ActorRefRoutee(r)
    }
    Router(new RedundancyRoutingLogic(3), routees)
  }

  def receive = {
    case Work => router.route(Work, sender())
    case Terminated(a) =>
      router = router.removeRoutee(a)
      val r = context.actorOf(Props[Worker])
      context watch r
      router = router.addRoutee(r)
  }
}

class RedundancyRoutingLogic(nbrCopies: Int) extends RoutingLogic {
  val roundRobin = RoundRobinRoutingLogic()

  def select(message: Any, routees: IndexedSeq[Routee]): Routee = {
    val targets = (1 to nbrCopies).map(_ => roundRobin.select(message, routees))
    SeveralRoutees(targets)
  }
}

object Master {

  case object Work

  def props() = Props(new Master)
}
