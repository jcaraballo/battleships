package org.casa.battleships.strategy.shooting

import org.casa.battleships.Position
import org.casa.battleships.Position.pos

class OneOneShooter extends Shooter{
  override def shoot(shootable: Set[Position], history: List[(Position, String)]): Option[Position] = Some(pos(1, 1))
}