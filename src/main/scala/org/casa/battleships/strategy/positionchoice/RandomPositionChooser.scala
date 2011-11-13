package org.casa.battleships.strategy.positionchoice

import org.casa.battleships.Position
import util.Random

class RandomPositionChooser extends PositionChooser{
  override def choose(positions: Set[Position]): Option[Position] = {
    val size: Int = positions.size
    if (size == 0) {
      None
    } else {
      Some(positions.toList(Random.nextInt(size)))
    }
  }
}