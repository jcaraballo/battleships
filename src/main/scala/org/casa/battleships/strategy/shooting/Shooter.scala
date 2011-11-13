package org.casa.battleships.strategy.shooting

import org.casa.battleships.Position

abstract class Shooter {
  def shoot(shootable: Set[Position], history: List[(Position, String)]): Option[Position]
}