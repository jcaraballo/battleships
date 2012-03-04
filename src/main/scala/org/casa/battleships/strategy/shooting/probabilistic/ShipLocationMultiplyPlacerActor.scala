package org.casa.battleships.strategy.shooting.probabilistic

import akka.actor.Actor
import org.casa.battleships.Position
import org.casa.battleships.fleet.ShipLocation
import org.casa.battleships.strategy.shooting.probabilistic.ShipLocationMultiplyPlacerActor.{Response, Request}

class ShipLocationMultiplyPlacerActor extends Actor{
  def receive = {
    case Request(fleetConfiguration, shipSize) => sender ! Response(ShipLocationMultiplyPlacer.findAllShipLocations(shipSize, fleetConfiguration.availability))
  }
}
object ShipLocationMultiplyPlacerActor{
  case class Request(fleetConfiguration: FleetConfiguration, shipSize: Int)
  case class Response(allShipLocations: Set[ShipLocation])
}