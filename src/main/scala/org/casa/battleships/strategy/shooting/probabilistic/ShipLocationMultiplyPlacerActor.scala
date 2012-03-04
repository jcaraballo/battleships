package org.casa.battleships.strategy.shooting.probabilistic

import akka.actor.Actor
import org.casa.battleships.strategy.shooting.probabilistic.ShipLocationMultiplyPlacerActor.{Response, Request}

class ShipLocationMultiplyPlacerActor extends Actor {
  def receive = {
    case Request(fleetConfiguration, shipSize) => {
      val allPossibleLocations = ShipLocationMultiplyPlacer.findAllShipLocations(shipSize, fleetConfiguration.availability)
      sender ! Response(allPossibleLocations.map(location => fleetConfiguration + location))
    }
  }
}
object ShipLocationMultiplyPlacerActor {
  case class Request(fleetConfiguration: FleetConfiguration, shipSize: Int)
  case class Response(allFleetConfigurations: Set[FleetConfiguration])
}