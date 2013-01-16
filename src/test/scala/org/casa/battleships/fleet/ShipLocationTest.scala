package org.casa.battleships.fleet

import org.junit.Test
import org.casa.battleships.Position.pos
import org.scalatest.junit.JUnitSuite

class ShipLocationTest extends JUnitSuite {
  @Test def verticalDownwardsPatrolBoat() {
    expectResult(Set(pos(1, 1), pos(1, 2))) {
      new ShipLocation(pos(1, 1), pos(1, 2)).squares
    }
  }

  @Test def verticalUpwardsPatrolBoat() {
    expectResult(Set(pos(1, 1), pos(1, 2))) {
      new ShipLocation(pos(1, 1), pos(1, 2)).squares
    }
  }

  @Test def horizontalLeftToRightPatrolBoat() {
    expectResult(Set(pos(1, 1), pos(2, 1))) {
      new ShipLocation(pos(1, 1), pos(2, 1)).squares
    }
  }

  @Test def horizontalRightToLeftPatrolBoat() {
    expectResult(Set(pos(2, 1), pos(1, 1))) {
      new ShipLocation(pos(2, 1), pos(1, 1)).squares
    }
  }

  @Test def singleSquareShipsAreNotAllowed() {
    try {
      new ShipLocation(pos(1, 1), pos(1, 1))
      fail()
    } catch {
      case e: IllegalArgumentException => Nil //expected
    }
  }

  @Test def isHorizontalIfAndOnlyIfAllPositionsAreInTheSameRow() {
    expectResult(true) {new ShipLocation(pos(1, 1), pos(5, 1)).isHorizontal}
    expectResult(false) {new ShipLocation(pos(1, 1), pos(1, 5)).isHorizontal}
  }
}