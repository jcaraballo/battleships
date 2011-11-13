package org.casa.battleships.ascii

object BoardPrinters {
  def createForUser = new BoardPrinter(ShipPrinters.createForUser)(' ', '·')
  def createForComputer = new BoardPrinter(ShipPrinters.createForComputer)(' ', '·')
}