package org.casa.battleships.ascii

import org.casa.battleships.{Board, Position}
import org.casa.battleships.Position.pos
import collection.mutable.ListBuffer

class BoardPrinter(val shipPrinter: ShipPrinter)(val water: Char, val shotWater: Char) {
  def horizontalAxis(board: Board) = "  " + (1 to board.size).map(_.toString.last).mkString(" ") + "  "
  def frame(board: Board) = "  " + ("~" * (board.size * 2 - 1)) + "  "

  def toAsciiStrings(board: Board): List[String] = {

    def squareToAscii(position: Position): Char = {
      (board.fleet.shipAt(position) match {
        case Some(ship) => shipPrinter.printShipSquareAt(ship, position)
        case None => if (board.shotPositions.contains(position)) shotWater else water
      })
    }

    val strings = new ListBuffer[String]
    strings += horizontalAxis(board)
    strings += frame(board)

    for (row <- 1 to board.size) {
      strings += row.toString.last + "{" +
          (1 to board.size).map(column => squareToAscii(pos(column, row))).mkString(" ") +
          "}" + row.toString.last
    }

    strings += frame(board)
    strings += horizontalAxis(board)
    strings.toList
  }

  def toAscii(board: Board): String = toAsciiStrings(board).mkString("\n")
}