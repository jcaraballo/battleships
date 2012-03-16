package org.casa.battleships.strategy.shooting.probabilistic

import collection.immutable.Set
import akka.pattern.ask
import akka.dispatch.Await
import akka.util.duration._
import MasterActor._
import akka.actor.{Props, Actor}
import akka.event.Logging
import akka.util.{Duration, Timeout}
import org.casa.battleships.fleet.{Bag, FleetLocation}

class MasterActor(workerFactory: ActorFactory)(shipSizes: Bag[Int]) extends Actor {
  import context._

  val log = Logging(system, this)

  private val duration: Duration = 1 second
  implicit val timeout = Timeout(duration)

  def receive = {
    case Request(fleetConfigurations) => {
      log.info("Entered Request(" + fleetConfigurations + ")")
      try {
        shipSizes.toList match {
          case nextShipSize :: restShipSizes => {
            val t: FleetConfiguration => Set[FleetLocation] = (fleetConfiguration: FleetConfiguration) => {
              val future = workerFactory.create ? WorkerActor.Request(fleetConfiguration, nextShipSize)
              val result = Await.result(future, duration).asInstanceOf[WorkerActor.Response]
              val clone = context.actorOf(Props(new MasterActor(workerFactory)(Bag.fromList(restShipSizes))))
              val allFleetsFuture = clone ? Request(result.allFleetConfigurations)
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
          sender ! akka.actor.Status.Failure(e)
          throw e
        }
      }
    }
  }
}

object MasterActor {
  case class Request(fleetConfigurations: Set[FleetConfiguration])
  case class Response(allFleetLocations: Set[FleetLocation])
}