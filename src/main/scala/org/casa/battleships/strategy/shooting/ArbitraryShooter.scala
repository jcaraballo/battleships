package org.casa.battleships.strategy.shooting

import org.casa.battleships.{ShotOutcome, Position}
import org.casa.battleships.strategy.positionchoice.PositionChooser

class ArbitraryShooter(val positionChooser: PositionChooser) extends Shooter {
  override def shoot(shootable: Set[Position], history: List[(Position, ShotOutcome.Value)]): Option[Position] = {
    positionChooser.choose(shootable)
  }
}