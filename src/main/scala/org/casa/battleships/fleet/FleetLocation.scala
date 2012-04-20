package org.casa.battleships.fleet

import org.casa.battleships.Position

case class FleetLocation(shipLocations: Set[ShipLocation]) {
  def shipSizes: Bag[Int] = Bag.fromList(shipLocations.toList.map(shipLocation => shipLocation.squares.size))

  def +(anotherShipLocation: ShipLocation): FleetLocation = new FleetLocation(shipLocations + anotherShipLocation)

  def subsetOf(other: FleetLocation): Boolean = shipLocations.subsetOf(other.shipLocations)
  def ⊆(other: FleetLocation) = subsetOf(other)

  def squares: Set[Position] = shipLocations.flatMap(_.squares)
}