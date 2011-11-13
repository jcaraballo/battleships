package org.casa.battleships.strategy.shooting

import org.casa.battleships.strategy.positionchoice.PositionChooser
import org.casa.battleships.{Positions, Position}

class AimAtNextToHitShooter(chooser: PositionChooser)(delegate: Shooter) extends Shooter {
  override def shoot(shootable: Set[Position], history: List[(Position, String)]): Option[Position] = {
    val lastNonWaterShot: Option[(Position, String)] = history.find(_._2 != "water")

    lastNonWaterShot match {
      case None => delegate.shoot(shootable, history)
      case Some((position: Position, outcome: String)) => {
        if (outcome == "hit") {
          chooser.choose(Positions.neighbours(position) & shootable) match {
            case Some(position) => Some(position)
            case _ => delegate.shoot(shootable, history)
          }
        } else {
          delegate.shoot(shootable, history)
        }
      }
    }
  }
}