package org.casa.battleships.ascii

import org.junit.Test
import org.casa.battleships.Position.pos
import org.scalatest.junit.JUnitSuite
import org.casa.battleships.fleet.{Ship, Fleet}
import org.casa.battleships.{Position, Board}

class AsciiDashboardTest extends JUnitSuite {
  @Test def displaysInAscii() {
    val smallFleet: Fleet = new Fleet(
        new Ship(pos(1, 1), pos(3, 1)),
        new Ship(pos(1, 2), pos(2, 2))
      )
    val shotPositions: Set[Position] = Set(pos(2, 4), pos(4, 4))
    val computerBoard: Board = new Board(4, smallFleet, shotPositions)
    val playerBoard: Board = new Board(4, smallFleet, shotPositions)

    expect(
      "  Computer     You      \n" +
      "  1 2 3 4      1 2 3 4  \n" +
      "  ~~~~~~~      ~~~~~~~  \n" +
      "1{       }1  1{< - >  }1\n" +
      "2{       }2  2{< >    }2\n" +
      "3{       }3  3{       }3\n" +
      "4{  ·   ·}4  4{  ·   ·}4\n" +
      "  ~~~~~~~      ~~~~~~~  \n" +
      "  1 2 3 4      1 2 3 4  \n"){
      new AsciiDashboard(computerBoard, playerBoard).toAscii
    }
  }
}