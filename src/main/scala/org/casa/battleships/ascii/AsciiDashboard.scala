package org.casa.battleships.ascii

import org.casa.battleships.Board

class AsciiDashboard(val computerBoard: Board, val userBoard: Board) {

  def pad(string: String, width: Int): String = string + " " * (width-string.size)

  def toAscii: String = {
    val computer: List[String] = BoardPrinters.createForComputer.toAsciiStrings(computerBoard)
    val player: List[String] = BoardPrinters.createForUser.toAsciiStrings(userBoard)

    val strings: List[String] = (computer zip player).map(z => z._1 + "  " + z._2)

    val widthPerBoard = computer.head.size

    pad("  Computer", widthPerBoard) + "  " + pad("  You", widthPerBoard) + "\n" + strings.mkString("\n") + "\n"
  }
}