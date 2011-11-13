package org.casa.battleships.ascii

import org.casa.battleships.Position.pos
import org.scalatest.junit.JUnitSuite
import org.casa.battleships.fleet._

import org.casa.battleships.Board
import org.junit.{Before, Test}

class BoardPrinterTest extends JUnitSuite {
  val board: Board = new Board(4, new Fleet(
    new Ship(pos(1, 1), pos(3, 1)),
    new Ship(pos(2, 2), pos(2, 3))
  ))

  @Before def shootABit(){
    board.shoot(pos(2, 1))
    board.shoot(pos(2, 2))
  }

  @Test def userBoard() {
    expect(List(
    "  1 2 3 4  ",
    "  ~~~~~~~  ",
    "1{< * >  }1",
    "2{  *    }2",
    "3{  v    }3",
    "4{       }4",
    "  ~~~~~~~  ",
    "  1 2 3 4  "
      ).mkString("\n")){
      BoardPrinters.createForUser.toAsciiStrings(board).mkString("\n")
    }
  }

  @Test def computerBoard() {
    expect(List(
      "  1 2 3 4  ",
      "  ~~~~~~~  ",
      "1{  *    }1",
      "2{  *    }2",
      "3{       }3",
      "4{       }4",
      "  ~~~~~~~  ",
      "  1 2 3 4  "
      )){
      BoardPrinters.createForComputer.toAsciiStrings(board)
    }
  }
}