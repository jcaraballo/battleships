package org.casa.battleships.fleet

case class FleetLocation(shipLocations: Set[ShipLocation]) {
  def +(anotherShipLocation: ShipLocation): FleetLocation = new FleetLocation(shipLocations + anotherShipLocation)
}