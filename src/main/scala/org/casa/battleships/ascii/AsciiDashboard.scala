package org.casa.battleships.ascii

import org.casa.battleships.Board

class AsciiDashboard(val playerAndHiddenBoard: (String, Board), val playerAndVisibleBoard: (String, Board)) {

  def pad(string: String, width: Int): String = string + " " * (width - string.size)

  def toAscii: String = {
    val (player1, hiddenBoard) = playerAndHiddenBoard
    val (player2, visibleBoard) = playerAndVisibleBoard
    val computer: List[String] = BoardPrinters.createForComputer.toAsciiStrings(hiddenBoard)
    val player: List[String] = BoardPrinters.createForUser.toAsciiStrings(visibleBoard)

    val strings: List[String] = (computer zip player).map(z => z._1 + "  " + z._2)

    val widthPerBoard = computer.head.size

    pad("  " + player1, widthPerBoard) + "  " + pad("  " + player2, widthPerBoard) + "\n" + strings.mkString("\n") + "\n"
  }
}