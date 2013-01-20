package org.casa.battleships.strategy.shooting.probabilistic

import collection.immutable.Set
import MasterActor._
import akka.event.Logging
import org.casa.battleships.fleet.{Bag, FleetLocation}
import akka.actor.{ActorRef, Actor}

class MasterActor(workerFactory: ActorFactory)(shipSizes: Bag[Int]) extends Actor {
  import context._

  val log = Logging(system, this)

  var fleets = Set[FleetConfiguration]()
  var originator: ActorRef = _

  def receive = {
    case Request(fleetConfigurations) =>
      originator = sender
//      log.info("Master received Request(" + fleetConfigurations + "), shipSizes: " + shipSizes)
      try {
        process(fleetConfigurations)
      }
      catch {
        case e => {
          originator ! akka.actor.Status.Failure(e)
          throw e
        }
      }

    case WorkerActor.Response(originalFleetConfiguration, newFleetConfigurations) => {
//      log.info("Master received WorkerActor.Response(" + newFleetConfigurations + ")")
      try {
//        log.info("Removing processed fleet configuration " + originalFleetConfiguration)
        fleets -= originalFleetConfiguration
//        log.info("Fleets have been updated to: " + fleets)
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
    val newFleetConfigurations = fleetConfigurations -- fleets

    fleets ++= newFleetConfigurations

    incompleteFleetConfigurations.foreach {
      fleet => {
        val requestForWorker = WorkerActor.Request(fleet, (shipSizes -- fleet.shipSizes).head)
        workerFactory.create ! requestForWorker
      }
    }

    if (incompleteFleetConfigurations.isEmpty) {
      val response = Response(fleets.map(_.location))
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