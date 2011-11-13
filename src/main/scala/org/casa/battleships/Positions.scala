package org.casa.battleships

import org.casa.battleships.Position._

object Positions {
  def neighbours(position: Position) = Set(position.up, position.right, position.down, position.left)

  def leftmost(ps: Iterable[Position]) = ps minBy (_.column)
  def rightmost(ps: Iterable[Position]) = ps maxBy (_.column)
  def upmost(ps: Iterable[Position]) = ps minBy (_.row)
  def downmost(ps: Iterable[Position]) = ps maxBy (_.row)

  def areHorizontal(ps: Iterable[Position]) = ps.forall(_.row == ps.head.row)

  def createGrid(size: Int): Set[Position] = {
    (1 to size).flatMap(row =>
      (1 to size).map(column => pos(row, column))
    ).toSet
  }
}