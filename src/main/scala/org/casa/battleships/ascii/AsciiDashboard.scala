package org.casa.battleships.ascii

import org.casa.battleships.Board

class AsciiDashboard(val computerBoard: Board, val userBoard: Board) {
  def toAscii: String = {
    val computer: List[String] = BoardPrinters.createForComputer.toAsciiStrings(computerBoard)
    val player: List[String] = BoardPrinters.createForUser.toAsciiStrings(userBoard)

    val strings: List[String] = (computer zip player).map(z => z._1 + "  " + z._2)
    "Comp   You\n" + strings.mkString("\n") + "\n"
  }
}