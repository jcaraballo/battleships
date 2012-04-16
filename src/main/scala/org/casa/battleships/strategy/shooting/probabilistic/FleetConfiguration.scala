package org.casa.battleships.strategy.shooting.probabilistic

import org.casa.battleships.Position
import org.casa.battleships.fleet.{Bag, ShipLocation, FleetLocation}

case class FleetConfiguration(location: FleetLocation, availability: Set[Position]) {
  def +(newShipLocation: ShipLocation) = FleetConfiguration(location + newShipLocation, availability -- newShipLocation.squares)

  def shipSizes: Bag[Int] = location.shipSizes

  def subsetOf(other: FleetConfiguration): Boolean = location subsetOf other.location
  def ⊆(other: FleetConfiguration) = subsetOf(other)
  
  override def toString: String = "FleetConfiguration(location: " + location + ", availability: " + availability + ")"
}

object FleetConfiguration {
  def apply(availability: Set[Position]): FleetConfiguration = FleetConfiguration(FleetLocation(Set()), availability)
}