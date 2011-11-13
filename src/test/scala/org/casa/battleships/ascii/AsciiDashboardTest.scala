package org.casa.battleships.ascii

import org.junit.Test
import org.casa.battleships.Position.pos
import org.scalatest.junit.JUnitSuite
import org.casa.battleships.Board
import org.casa.battleships.fleet.{Ship, Fleet}

class AsciiDashboardTest extends JUnitSuite {
  @Test def displaysInAscii() {
    val smallFleet: Fleet = new Fleet(
        new Ship(pos(1, 1), pos(3, 1)),
        new Ship(pos(1, 2), pos(2, 2))
      )
    val computerBoard: Board = new Board(4, smallFleet)
    val playerBoard: Board = new Board(4, smallFleet)

    expect("""Comp   You
_____  _____
 1234   1234
1      1<->·
2      2<>··
3      3····
4      4····
_____  _____
"""){
      new AsciiDashboard(computerBoard, playerBoard).toAscii
    }
  }
}