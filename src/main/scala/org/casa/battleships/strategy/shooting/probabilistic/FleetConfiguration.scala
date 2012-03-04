package org.casa.battleships.strategy.shooting.probabilistic

import org.casa.battleships.Position
import org.casa.battleships.fleet.{ShipLocation, FleetLocation}

case class FleetConfiguration(fleet: FleetLocation, availability: Set[Position]) {
  def +(newShipLocation: ShipLocation) = FleetConfiguration(fleet + newShipLocation, availability -- newShipLocation.squares)
}

object FleetConfiguration {
  def apply(availability: Set[Position]): FleetConfiguration = FleetConfiguration(FleetLocation(Set()), availability)
}