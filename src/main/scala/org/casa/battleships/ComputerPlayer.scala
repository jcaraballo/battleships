package org.casa.battleships

import strategy.FleetComposer
import strategy.positionchoice.PositionChooser
import strategy.shooting.Shooter
import collection.immutable.{Nil, List}

class ComputerPlayer(val positionChooser: PositionChooser, val shooter: Shooter, val boardSize: Int, val shipSizes: List[Int]) {
  val board: Board = new Board(boardSize, new FleetComposer(positionChooser).create(boardSize, shipSizes).get)
  var myLastShotAtTheEnemy: Option[Position] = None
  var historyOfMyShotsAtTheEnemy: List[(Position, ShotOutcome.Value)] = Nil

  def playFirstTurn(firstShot: Position): Turn = {
    val outcome: ShotOutcome.Value = board.shoot(firstShot)
    val shotBack: Position = shooter.shoot(enemyPositionsToBeShot, historyOfMyShotsAtTheEnemy).get

    myLastShotAtTheEnemy = Some(shotBack)

    Turn(outcome, shotBack)
  }

  def play(enemiesTurn: Turn): Turn = {
    historyOfMyShotsAtTheEnemy = (myLastShotAtTheEnemy.get, enemiesTurn.lastShotOutcome) :: historyOfMyShotsAtTheEnemy
    val outcomeOfTheLastShotFromTheEnemyToMe: ShotOutcome.Value = board.shoot(enemiesTurn.shotBack)

    val shotBack: Position = shooter.shoot(enemyPositionsToBeShot, historyOfMyShotsAtTheEnemy).get

    myLastShotAtTheEnemy = Some(shotBack)

    Turn(outcomeOfTheLastShotFromTheEnemyToMe, shotBack)
  }

  private def enemyPositionsToBeShot = Positions.createGrid(boardSize) -- historyOfMyShotsAtTheEnemy.map(_._1).toSet
}