package org.casa.battleships.strategy.shooting

import org.casa.battleships.strategy.positionchoice.PositionChooser
import org.casa.battleships.{ShotOutcome, Positions, Position}
import org.casa.battleships.ShotOutcome._

class AimAtNextToHitShooter(chooser: PositionChooser)(delegate: Shooter) extends Shooter {
  override def shoot(shootable: Set[Position], history: List[(Position, ShotOutcome.Value)]): Option[Position] = {
    val lastNonWaterShot: Option[(Position, ShotOutcome.Value)] = history.find(_._2 != Water)

    lastNonWaterShot match {
      case None => delegate.shoot(shootable, history)
      case Some((position: Position, outcome: ShotOutcome.Value)) => {
        if (outcome == Hit) {
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