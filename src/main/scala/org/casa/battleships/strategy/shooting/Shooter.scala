package org.casa.battleships.strategy.shooting

import org.casa.battleships.{ShotOutcome, Position}

trait Shooter {
  def shoot(shootable: Set[Position], history: List[(Position, ShotOutcome.Value)]): Option[Position]
}