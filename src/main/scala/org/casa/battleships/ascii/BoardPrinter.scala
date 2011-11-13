package org.casa.battleships.ascii

import org.casa.battleships.{Board, Position}
import org.casa.battleships.Position.pos
import collection.mutable.ListBuffer

class BoardPrinter(val shipPrinter: ShipPrinter)(val water: Char, val shotWater: Char) {
  def frame(board: Board) = "_" * (board.size + 1)
  def topAxis(board: Board) = " " + (1 to board.size).map(_.toString.last).mkString

  def toAsciiStrings(board: Board): List[String] = {
    val strings = new ListBuffer[String]
    strings += frame(board)
    strings += topAxis(board)

    for (row <- 1 to board.size) {
      val stringBuilder: StringBuilder = new StringBuilder()
      stringBuilder += row.toString.last
      for (column <- 1 to board.size) {
        val position: Position = pos(column, row)
        stringBuilder += (board.fleet.shipAt(position) match {
          case Some(ship) => shipPrinter.printShipSquareAt(ship, position)
          case None => if (board.shotPositions.contains(position)) shotWater else water
        })
      }
      strings += stringBuilder.toString()
    }

    strings += frame(board)
    strings.toList
  }

  def toAscii(board: Board): String = toAsciiStrings(board).mkString("\n")
}