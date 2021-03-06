package testtools.fixtures

import org.casa.battleships.Position._
import org.casa.battleships.fleet.Ship.immaculateShip
import org.casa.battleships.fleet._

object Examples {
  def someFleet: Fleet = new Fleet(
      immaculateShip(pos(1, 1), pos(5, 1)),
      immaculateShip(pos(1, 2), pos(4, 2)),
      immaculateShip(pos(1, 3), pos(3, 3)),
      immaculateShip(pos(1, 4), pos(3, 4)),
      immaculateShip(pos(1, 5), pos(2, 5))
    )

  def classicListOfShipSizes = List(5, 4, 3, 3, 2)
  def classicBagOfShipSizes = Bag(5, 4, 3, 3, 2)
  def someListOfShipSizes = classicListOfShipSizes

  def somePosition = pos(1, 1)
  
  def someAvailability = Set(pos(10, 9), pos(10, 10))
  def someOtherAvailability = Set(pos(8, 9), pos(8, 10))

  def someShipSize = 2

  def someFleetLocation = FleetLocation(someFleet.ships.map(_.location))

  def someOtherFleetLocation = FleetLocation(Set(
    new ShipLocation(pos(1, 6), pos(5, 6)),
    new ShipLocation(pos(1, 7), pos(4, 7)),
    new ShipLocation(pos(1, 8), pos(3, 8)),
    new ShipLocation(pos(1, 9), pos(3, 9)),
    new ShipLocation(pos(1, 10), pos(2, 10))
  ))
}