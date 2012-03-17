package org.casa.battleships.strategy.shooting.probabilistic

import akka.actor.Actor
import org.casa.battleships.strategy.shooting.probabilistic.WorkerActor.{Response, Request}

class WorkerActor(shipPlacer: ShipLocationMultiplyPlacer) extends Actor {
  def receive = {
    case Request(fleetConfiguration, shipSize) => {
      val allPossibleLocations = shipPlacer.findAllShipLocations(shipSize, fleetConfiguration.availability)
      sender ! Response(fleetConfiguration, allPossibleLocations.map(location => fleetConfiguration + location))
    }
  }
}
object WorkerActor {
  case class Request(fleetConfiguration: FleetConfiguration, shipSize: Int)
  case class Response(originalFleetConfiguration: FleetConfiguration, allFleetConfigurations: Set[FleetConfiguration])
}