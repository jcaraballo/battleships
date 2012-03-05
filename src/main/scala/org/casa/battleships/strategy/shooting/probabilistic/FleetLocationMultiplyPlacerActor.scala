package org.casa.battleships.strategy.shooting.probabilistic

import org.casa.battleships.Position
import collection.immutable.Set
import org.casa.battleships.fleet.FleetLocation
import akka.pattern.ask
import akka.dispatch.Await
import akka.util.duration._
import FleetLocationMultiplyPlacerActor._
import akka.actor.{ActorRef, Props, Actor}
import akka.event.Logging
import akka.util.{Duration, Timeout}

class FleetLocationMultiplyPlacerActor(workerActor: ActorRef) extends Actor {

  import context._

  val log = Logging(system, this)

  private val duration: Duration = 1 second
  implicit val timeout = Timeout(duration)

  def receive = {
    case Request(shipSizes, available) => {
      log.info("Entered Request(" + shipSizes + ", " + available + ")")
      try {
        val future = context.actorOf(Props(new FleetLocationMultiplyPlacerActor(workerActor))) ? SelfRequest(shipSizes, Set(FleetConfiguration(available)))
        sender ! Await.result(future, duration).asInstanceOf[GenericResponse]
      }
      catch {
        case e => {
          log.info("While processing Request(" + shipSizes + ", " + available + ") exception was thrown, sending back wrapped in ExceptionalReponse: " + e.getMessage + ", cause: " + e.getCause + ", stack trace: " + e.getStackTrace)
          sender ! ExceptionalResponse(e)
        }
      }
    }

    case SelfRequest(shipSizes, fleetConfigurations) => {
      log.info("Entered SelfRequest(" + shipSizes + ", " + fleetConfigurations + ")")
      try {
        shipSizes match {
          case nextShipSize :: restShipSizes => {
            val t: FleetConfiguration => Set[FleetLocation] = (fleetConfiguration: FleetConfiguration) => {
              val future = workerActor ? WorkerActor.Request(fleetConfiguration, nextShipSize)
              val result = Await.result(future, duration).asInstanceOf[WorkerActor.Response]
              val clone = context.actorOf(Props(new FleetLocationMultiplyPlacerActor(workerActor)))
              val allFleetsFuture = clone ? SelfRequest(restShipSizes, result.allFleetConfigurations)
              Await.result(allFleetsFuture, duration) match {
                case response: Response => response.allFleetLocations
                case exceptional: ExceptionalResponse => sender ! exceptional; Set() //should kill myself
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
          log.info("While processing SelfRequest("  + shipSizes + ", " + fleetConfigurations + ") exception was thrown, sending back wrapped in ExceptionalReponse: " + e.getMessage + ", cause: " + e.getCause + ", stack trace: " + e.getStackTrace)
          sender ! ExceptionalResponse(e)
        }
      }
    }
  }
}

object FleetLocationMultiplyPlacerActor {

  case class Request(shipSizes: List[Int], availability: Set[Position])
  case class SelfRequest(shipSizes: List[Int], fleetConfigurations: Set[FleetConfiguration])

  case class GenericResponse()
  case class Response(allFleetLocations: Set[FleetLocation]) extends GenericResponse
  case class ExceptionalResponse(exception: Throwable) extends GenericResponse
}