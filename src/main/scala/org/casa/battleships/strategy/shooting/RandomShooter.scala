package org.casa.battleships.strategy.shooting

import org.casa.battleships.strategy.positionchoice.RandomPositionChooser
import org.casa.battleships.{ShotOutcome, Position}

class RandomShooter extends Shooter{
  override def shoot(shootable: Set[Position], history: List[(Position, ShotOutcome.Value)]): Option[Position] = {
    new RandomPositionChooser().choose(shootable)
  }
}