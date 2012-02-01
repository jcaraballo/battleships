package org.casa.battleships.strategy.shooting.probabilistic

import org.casa.battleships.Position
import collection.immutable.Set
import org.casa.battleships.fleet.{ShipLocation, FleetLocation}
import akka.pattern.ask
import akka.dispatch.Await
import akka.util.duration._
import akka.util.{FiniteDuration, Timeout}
import FleetLocationMultiplyPlacerActor._
import akka.actor.{ActorRef, Props, Actor}
import java.util.concurrent.TimeoutException
import akka.event.Logging

class FleetLocationMultiplyPlacerActor(shipsPlacerActor: ActorRef) extends Actor {

  import context._

  val log = Logging(system, this)

  private val duration: FiniteDuration = 1 second
  implicit val timeout = Timeout(duration)

  def receive = {
    case Request(shipSizes, available) => {
      log.info("Entered Request(" + shipSizes + ", " + available + ")")
      try {
            val future = context.actorOf(Props(new FleetLocationMultiplyPlacerActor(shipsPlacerActor))) ? SelfRequest(shipSizes, Set((new FleetLocation(Set()), available)))
            val allFleetLocations = Await.result(future, duration).asInstanceOf[Response].allFleetLocations

            sender ! Response(allFleetLocations)
      }
      catch {
        case e: TimeoutException => {
          log.info("Timed out while processing Request(" + shipSizes + ", " + available + "): " + e.getMessage + ", because: " + e.getCause + ", stack trace: " + e.getStackTrace + ", rethrowing")
          sender ! ExceptionalResponse(e)
        }
      }
    }

    case SelfRequest(shipSizes, validFleetWithWhatLeavesAvailable) => {
      log.info("Entered SelfRequest(" + shipSizes + ", " + validFleetWithWhatLeavesAvailable + ")")
      try {
        shipSizes match {
          case nextShipSize :: restShipSizes => {
            val t: (FleetLocation, Set[Position]) => Set[FleetLocation] = (fleet: FleetLocation, available: Set[Position]) => {
              val future = shipsPlacerActor ? ShipLocationMultiplyPlacerActor.Request(nextShipSize, available)
              val result = Await.result(future, duration).asInstanceOf[ShipLocationMultiplyPlacerActor.Response]
              val lol: Set[(FleetLocation, Set[Position])] = result.allShipLocations.map {
                (possibleLocation: ShipLocation) => (fleet + possibleLocation, available -- possibleLocation.squares)
              }

              val future1 = context.actorOf(Props(new FleetLocationMultiplyPlacerActor(shipsPlacerActor))) ? SelfRequest(restShipSizes, lol)
              Await.result(future1, duration).asInstanceOf[Response].allFleetLocations
            }
            sender ! Response(validFleetWithWhatLeavesAvailable.flatMap(t.tupled))
          }
          case empty => sender ! Response(validFleetWithWhatLeavesAvailable.map(_._1))
        }
      }
      catch {
        case e: TimeoutException => {
          log.info("Timed out while processing SelfRequest(" + shipSizes + ", " + validFleetWithWhatLeavesAvailable + "): " + e.getMessage + ", because: " + e.getCause + ", stack trace: " + e.getStackTrace + ", rethrowing")
          sender ! ExceptionalResponse(e)
        }
      }
    }
  }
}

object FleetLocationMultiplyPlacerActor {

  case class Request(shipSizes: List[Int], available: Set[Position])

  case class Response(allFleetLocations: Set[FleetLocation])

  case class ExceptionalResponse(exception: Exception)

  case class SelfRequest(shipSizes: List[Int], validFleetWithWhatLeavesAvailable: Set[(FleetLocation, Set[Position])])

}