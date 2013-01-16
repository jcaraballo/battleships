package org.casa.battleships.strategy

import org.junit.Test
import org.casa.battleships.Position.pos
import org.scalatest.junit.JUnitSuite
import org.casa.battleships.strategy.positionchoice.UpmostAndThenLeftmostPositionChooser
import org.casa.battleships.fleet.Fleet
import org.casa.battleships.fleet.Ship.immaculateShip

class FleetComposerTest extends JUnitSuite {
  val chooser = new UpmostAndThenLeftmostPositionChooser

  @Test def createsFleet() {
    val expectedFleet: Fleet = new Fleet(
      immaculateShip(pos(1, 1), pos(5, 1)),
      immaculateShip(pos(6, 1), pos(9, 1)),
      immaculateShip(pos(10, 1), pos(10, 3)),
      immaculateShip(pos(1, 2), pos(3, 2)),
      immaculateShip(pos(4, 2), pos(5, 2))
    )
    expectResult(Some(expectedFleet)) {
      new FleetComposer(chooser).create(10, List(5, 4, 3, 3, 2))
    }
  }
}