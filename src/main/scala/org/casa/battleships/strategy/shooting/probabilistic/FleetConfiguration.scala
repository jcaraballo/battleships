package org.casa.battleships.strategy.shooting.probabilistic

import org.casa.battleships.fleet.FleetLocation
import org.casa.battleships.Position

case class FleetConfiguration(fleet: FleetLocation, availability: Set[Position])

object FleetConfiguration{
  def apply(availability: Set[Position]) = new FleetConfiguration(new FleetLocation(Set()), availability)
}