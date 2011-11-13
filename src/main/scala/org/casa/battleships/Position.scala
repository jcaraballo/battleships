package org.casa.battleships

case class Position(column: Int, row: Int) {
  def left = Position(column - 1, row)
  def right = Position(column + 1, row)
  def up = Position(column, row - 1)
  def down = Position(column, row + 1)

  def toAscii = "(" + column + ", " + row + ")"

  override def toString = toAscii
}

object Position {
  def pos(column: Int, row: Int) = Position(column, row)
}