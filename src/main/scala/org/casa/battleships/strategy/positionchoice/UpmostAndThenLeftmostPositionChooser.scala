package org.casa.battleships.strategy.positionchoice

import org.casa.battleships.Position

class UpmostAndThenLeftmostPositionChooser extends PositionChooser {
  override def choose(positions: Set[Position]) = {
    val sortedPositions = positions.toList.sortWith((a, b) => a.row < b.row || (a.row == b.row) && a.column < b.column)
    sortedPositions.headOption
  }
}