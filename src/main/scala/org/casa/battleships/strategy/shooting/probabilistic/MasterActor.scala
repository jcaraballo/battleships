package org.casa.battleships.strategy.shooting.probabilistic

import collection.immutable.Set
import akka.util.duration._
import MasterActor._
import akka.event.Logging
import akka.util.{Duration, Timeout}
import org.casa.battleships.fleet.{Bag, FleetLocation}
import akka.actor.{ActorRef, Actor}

class MasterActor(workerFactory: ActorFactory)(shipSizes: Bag[Int]) extends Actor {
  import context._

  val log = Logging(system, this)

  private val duration: Duration = 1 second
  implicit val timeout = Timeout(duration)

  var fleets = Set[FleetConfiguration]()
  var originator: ActorRef = _

  def receive = {
    case Request(fleetConfigurations) => {
      originator = sender
      log.info("Master received Request(" + fleetConfigurations + "), shipSizes: " + shipSizes)
      try {
        process(fleetConfigurations)
      }
      catch {
        case e => {
          originator ! akka.actor.Status.Failure(e)
          throw e
        }
      }
    }

    case WorkerActor.Response(originalFleetConfiguration, newFleetConfigurations) => {
      log.info("Master received WorkerActor.Response(" + newFleetConfigurations + ")")
      try {
        log.info("Removing processed fleet configuration " + originalFleetConfiguration)
        fleets -= originalFleetConfiguration
        log.info("Fleets have been updated to: " + fleets)
        process(newFleetConfigurations)
      }
      catch {
        case e => {
          originator ! akka.actor.Status.Failure(e)
          throw e
        }
      }
    }
  }

  private def process(fleetConfigurations: Set[FleetConfiguration]) {
    log.info("Entering process(" + fleetConfigurations + ")")
    val newFleetConfigurations = fleetConfigurations -- fleets

    log.info("newFleetConfigurations: " + newFleetConfigurations)

    fleets ++= newFleetConfigurations

    log.info("fleets updated to: " + fleets)

    log.info("incomplete: " + incompleteFleetConfigurations)
    
    incompleteFleetConfigurations.foreach {
      fleet => {
        log.info("For fleet: " + fleet)
        val requestForWorker = WorkerActor.Request(fleet, (shipSizes -- fleet.shipSizes).head)
        log.info("Calling worker with " + requestForWorker)
        workerFactory.create ! requestForWorker
        log.info("Called worker with " + requestForWorker)
      }
    }

    if (incompleteFleetConfigurations.isEmpty) {
      val response = Response(fleets.map(_.location))
      log.info("incompleteFleetConfigurations is empty, exiting with response: " + response)
      originator ! response
    }
  }

  private def incompleteFleetConfigurations: Set[FleetConfiguration] = {
    fleets.filter((fleet: FleetConfiguration) => fleet.shipSizes != shipSizes)
  }
}

object MasterActor {
  case class Request(fleetConfigurations: Set[FleetConfiguration])
  case class Response(allFleetLocations: Set[FleetLocation])
}