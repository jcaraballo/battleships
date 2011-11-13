package org.casa.battleships.strategy.shooting

import org.casa.battleships.Position
import org.casa.battleships.strategy.positionchoice.RandomPositionChooser

class RandomShooter extends Shooter{
  override def shoot(shootable: Set[Position], history: List[(Position, String)]): Option[Position] = new RandomPositionChooser().choose(shootable)
}