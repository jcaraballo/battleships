package org.casa.battleships

import strategy.shooting.Shooter
import scala.collection.immutable.List
import scala._

class ComputerPlayer(val shooter: Shooter, val boardSize: Int) {
  def play(historyOfComputerOnHumanShots: List[(Position, ShotOutcome.Value)]): Position = {
    val computerOnHumanShot = shooter.shoot(Positions.createGrid(boardSize) -- historyOfComputerOnHumanShots.map(_._1), historyOfComputerOnHumanShots).get
    computerOnHumanShot
  }
}