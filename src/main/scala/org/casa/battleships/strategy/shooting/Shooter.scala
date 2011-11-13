package org.casa.battleships.strategy.shooting

import org.casa.battleships.{ShotOutcome, Position}

abstract class Shooter {
  def shoot(shootable: Set[Position], history: List[(Position, ShotOutcome.Value)]): Option[Position]
}