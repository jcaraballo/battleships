package org.casa.battleships.strategy.shooting

import org.casa.battleships.strategy.positionchoice.PositionChooser

object Shooters {
  def bestShooter(positionChooser: PositionChooser): Shooter = {
    new SequentialShooter(
      new LinesShooter(positionChooser),
      new AimAtNextToHitShooter(positionChooser),
      new ArbitraryShooter(positionChooser))
  }
}