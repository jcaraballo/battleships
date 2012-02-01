package org.casa.battleships.strategy.shooting.probabilistic

import org.casa.battleships.Position
import collection.immutable.Set
import org.casa.battleships.fleet.{ShipLocation, FleetLocation}
import akka.pattern.ask
import akka.dispatch.Await
import akka.util.duration._
import FleetLocationMultiplyPlacerActor._
import akka.actor.{ActorRef, Props, Actor}
import java.util.concurrent.TimeoutException
import akka.event.Logging
import akka.util.{Duration, Timeout}

class FleetLocationMultiplyPlacerActor(shipsPlacerActor: ActorRef) extends Actor {

  import context._

  val log = Logging(system, this)

  private val duration: Duration = 1 second
  implicit val timeout = Timeout(duration)

  def receive = {
    case Request(shipSizes, available) => {
      log.info("Entered Request(" + shipSizes + ", " + available + ")")
      try {
        val future = context.actorOf(Props(new FleetLocationMultiplyPlacerActor(shipsPlacerActor))) ? SelfRequest(shipSizes, Set((new FleetLocation(Set()), available)))
        sender ! Await.result(future, duration).asInstanceOf[GenericResponse]
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
              Await.result(future1, duration) match {
                case response: Response => response.allFleetLocations
                case exceptional: ExceptionalResponse => sender ! exceptional; Set() //should kill myself
                case e => log.error("Unexpected"); throw new IllegalArgumentException
              }
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
  case class SelfRequest(shipSizes: List[Int], validFleetWithWhatLeavesAvailable: Set[(FleetLocation, Set[Position])])

  case class GenericResponse()
  case class Response(allFleetLocations: Set[FleetLocation]) extends GenericResponse
  case class ExceptionalResponse(exception: Exception) extends GenericResponse
}