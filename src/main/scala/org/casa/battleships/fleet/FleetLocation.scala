package org.casa.battleships.fleet

case class FleetLocation(shipLocations: Set[ShipLocation]) {
  def shipSizes: List[Int] = shipLocations.toList.map(shipLocation => shipLocation.squares.size)

  def +(anotherShipLocation: ShipLocation): FleetLocation = new FleetLocation(shipLocations + anotherShipLocation)
}