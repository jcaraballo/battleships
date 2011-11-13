package org.casa.battleships.strategy.shooting

import org.casa.battleships.Position.pos
import org.casa.battleships.{ShotOutcome, Position}

class OneOneShooter extends Shooter{
  override def shoot(shootable: Set[Position], history: List[(Position, ShotOutcome.Value)]): Option[Position] = Some(pos(1, 1))
}