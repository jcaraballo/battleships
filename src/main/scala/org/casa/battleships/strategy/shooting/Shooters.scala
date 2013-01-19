package org.casa.battleships.strategy.shooting

import org.casa.battleships.strategy.positionchoice.PositionChooser
import akka.actor.ActorSystem
import org.casa.battleships.fleet.Bag
import concurrent.duration.FiniteDuration

object Shooters {
  def bestShooter(positionChooser: PositionChooser): Shooter = {
    new SequentialShooter(
      new LinesShooter(positionChooser),
      new AimAtNextToHitShooter(positionChooser),
      new ArbitraryShooter(positionChooser))
  }

  def newBestShooter(chooser: PositionChooser, actorSystem: ActorSystem, shipSizes: Bag[Int], timeoutDuration: FiniteDuration): Shooter = {
    new NewBestShooter(chooser, actorSystem, shipSizes, timeoutDuration)
  }
}