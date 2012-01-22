package org.casa.battleships.strategy.shooting

import org.casa.battleships.strategy.positionchoice.RandomPositionChooser

object Shooters {
  def bestShooter: SequentialShooter = {
    new SequentialShooter(
      new LinesShooter(new RandomPositionChooser),
      new AimAtNextToHitShooter(new RandomPositionChooser),
      new RandomShooter)
  }
}