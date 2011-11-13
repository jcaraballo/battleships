package org.casa.battleships.ascii

object ShipPrinters {
  def createForUser = new ShipPrinter('<', '-', '>', '^', '|', 'v', '*')
  def createForComputer = new ShipPrinter(' ', ' ', ' ', ' ', ' ', ' ', '*')
}