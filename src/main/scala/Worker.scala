import akka.actor.Actor

/**
  * Created by hai on 6/13/2017.
  */
class Worker extends Actor {

  import Master._

  def receive = {
    case Work => println("working" + context.self.path.name)
  }

}

