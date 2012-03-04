package org.casa.battleships.strategy.shooting.probabilistic

import akka.actor.Actor
import org.casa.battleships.Position
import org.casa.battleships.fleet.ShipLocation
import org.casa.battleships.strategy.shooting.probabilistic.ShipLocationMultiplyPlacerActor.{Response, Request}

class ShipLocationMultiplyPlacerActor extends Actor{
  def receive = {
    case Request(shipSize, available) => sender ! Response(ShipLocationMultiplyPlacer.findAllShipLocations(shipSize, available))
  }
}
object ShipLocationMultiplyPlacerActor{
  case class Request(shipSize: Int, availability: Set[Position])
  case class Response(allShipLocations: Set[ShipLocation])
}