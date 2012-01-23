package org.casa.battleships.strategy.shooting

import org.casa.battleships.strategy.positionchoice.PositionChooser

object Shooters {
  def bestShooter(positionChooser: PositionChooser): SequentialShooter = {
    new SequentialShooter(
      new LinesShooter(positionChooser),
      new AimAtNextToHitShooter(positionChooser),
      new ArbitraryShooter(positionChooser))
  }
}