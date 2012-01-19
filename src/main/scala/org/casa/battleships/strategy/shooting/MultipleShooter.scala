package org.casa.battleships.strategy.shooting

import org.casa.battleships.{ShotOutcome, Position}

class MultipleShooter(shooters: Shooter*) extends Shooter {
  def shoot(shootable: Set[Position], history: List[(Position, ShotOutcome.Value)]): Option[Position] = {
    for (shooter <- shooters) {
      val target: Option[Position] = shooter.shoot(shootable, history)
      if(target.isDefined) return target
    }

    None
  }
}