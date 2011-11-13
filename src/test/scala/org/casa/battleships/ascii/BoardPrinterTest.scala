package org.casa.battleships.ascii

import org.casa.battleships.Position.pos
import org.scalatest.junit.JUnitSuite
import org.casa.battleships.fleet._

import org.casa.battleships.Board
import org.junit.{Before, Test}

class BoardPrinterTest extends JUnitSuite {
  val board: Board = new Board(4, new Fleet(
    Submarine(pos(1, 1), pos(3, 1)),
    PatrolBoat(pos(2, 2), pos(2, 3))
  ))

  @Before def shootABit(){
    board.shoot(pos(2, 1))
    board.shoot(pos(2, 2))
  }

  @Test def userBoard() {
    expect(List(
        "_____",
        " 1234",
        "1<*>·",
        "2·*··",
        "3·v··",
        "4····",
        "_____"
      )){
      BoardPrinters.createForUser.toAsciiStrings(board)
    }
  }

  @Test def computerBoard() {
    expect(List(
        "_____",
        " 1234",
        "1 *  ",
        "2 *  ",
        "3    ",
        "4    ",
        "_____"
      )){
      BoardPrinters.createForComputer.toAsciiStrings(board)
    }
  }
}