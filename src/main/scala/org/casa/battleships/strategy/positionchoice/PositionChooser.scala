package org.casa.battleships.strategy.positionchoice

import org.casa.battleships.Position

trait PositionChooser {
  def choose(positions: Set[Position]): Option[Position]
}