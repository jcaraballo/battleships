package org.casa.battleships.strategy.positionchoice

import org.casa.battleships.Position

abstract class PositionChooser {
  def choose(positions: Set[Position]): Option[Position]
}