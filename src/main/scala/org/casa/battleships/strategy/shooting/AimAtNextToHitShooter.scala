package org.casa.battleships.strategy.shooting

import org.casa.battleships.strategy.positionchoice.PositionChooser
import org.casa.battleships.{ShotOutcome, Positions, Position}
import org.casa.battleships.ShotOutcome._

class AimAtNextToHitShooter(chooser: PositionChooser) extends Shooter {
  override def shoot(shootable: Set[Position], history: List[(Position, ShotOutcome.Value)]): Option[Position] = {
    val lastNonWaterShot: Option[(Position, ShotOutcome.Value)] = history.find(_._2 != Water)

    lastNonWaterShot match {
      case Some((position: Position, Hit)) => {
        chooser.choose(Positions.neighbours(position) & shootable)
      }
      case _ => None
    }
  }
}