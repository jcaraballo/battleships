package org.casa.battleships.strategy.shooting.probabilistic

import org.casa.battleships.Position
import collection.immutable.Set
import org.casa.battleships.fleet.FleetLocation
import akka.pattern.ask
import akka.dispatch.Await
import akka.util.duration._
import MasterActor._
import akka.actor.{ActorRef, Props, Actor}
import akka.event.Logging
import akka.util.{Duration, Timeout}

class MasterActor(workerActor: ActorRef) extends Actor {
  import context._

  val log = Logging(system, this)

  private val duration: Duration = 1 second
  implicit val timeout = Timeout(duration)

  def receive = {
    case Request(shipSizes, fleetConfigurations) => {
      log.info("Entered Request(" + shipSizes + ", " + fleetConfigurations + ")")
      try {
        shipSizes match {
          case nextShipSize :: restShipSizes => {
            val t: FleetConfiguration => Set[FleetLocation] = (fleetConfiguration: FleetConfiguration) => {
              val future = workerActor ? WorkerActor.Request(fleetConfiguration, nextShipSize)
              val result = Await.result(future, duration).asInstanceOf[WorkerActor.Response]
              val clone = context.actorOf(Props(new MasterActor(workerActor)))
              val allFleetsFuture = clone ? Request(restShipSizes, result.allFleetConfigurations)
              Await.result(allFleetsFuture, duration) match {
                case response: Response => response.allFleetLocations
                case e => log.error("Unexpected"); throw new IllegalArgumentException
              }
            }
            sender ! Response(fleetConfigurations.flatMap(t))
          }
          case empty => sender ! Response(fleetConfigurations.map(_.fleet))
        }
      }
      catch {
        case e => {
          log.info("While processing Request("  + shipSizes + ", " + fleetConfigurations + ") exception was thrown: " +
            e.getMessage + ", cause: " + e.getCause + ", stack trace: " + e.getStackTrace)
          sender ! akka.actor.Status.Failure(e)
          throw e
        }
      }
    }
  }
}

object MasterActor {
  case class Request(shipSizes: List[Int], fleetConfigurations: Set[FleetConfiguration])
  case class Response(allFleetLocations: Set[FleetLocation])
}