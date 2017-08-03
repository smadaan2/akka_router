import akka.actor.ActorSystem

/**
  * Created by hai on 6/13/2017.
  */
object RouterApp extends App {

  import Master._

  val system = ActorSystem("Practice")
  val master = system.actorOf(Master.props(), "master")
  master ! Work
}
