package testtools.matching

import org.casa.battleships.fleet.{Ship, Fleet}
import org.casa.battleships.Position._
import org.casa.battleships.fleet.Ship.immaculateShip

object Examples {
  def someFleet: Fleet = new Fleet(
      immaculateShip(pos(1, 1), pos(5, 1)),
      immaculateShip(pos(1, 2), pos(4, 2)),
      immaculateShip(pos(1, 3), pos(3, 3)),
      immaculateShip(pos(1, 4), pos(3, 4)),
      immaculateShip(pos(1, 5), pos(2, 5))
    )
}