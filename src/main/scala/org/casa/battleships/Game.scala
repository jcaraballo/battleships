package org.casa.battleships

import strategy.FleetComposer
import strategy.positionchoice.PositionChooser
import strategy.shooting.Shooter
import collection.immutable.{Nil, List}

class Game(val boardSize: Int, val shipSizes: List[Int], val positionChooser: PositionChooser, val shooter: Shooter) {
  val computerBoard: Board = new Board(boardSize, new FleetComposer(positionChooser).create(boardSize, shipSizes).get)
  var lastShot: Option[Position] = None
  var historyOfMyShotsToTheEnemy: List[(Position, ShotOutcome.Value)] = Nil

  def playFirstTurn(firstShot: Position): Turn = {
    val outcome: ShotOutcome.Value = computerBoard.shoot(firstShot)
    val shotBack: Position = shooter.shoot(userShootablePositions, historyOfMyShotsToTheEnemy).get

    lastShot = Some(shotBack)

    Turn(outcome, shotBack)
  }

  def play(enemiesTurn: Turn): Turn = {
    historyOfMyShotsToTheEnemy = (lastShot.get, enemiesTurn.lastShotOutcome) :: historyOfMyShotsToTheEnemy
    val outcomeOfTheLastShotFromTheEnemyToMe: ShotOutcome.Value = computerBoard.shoot(enemiesTurn.shotBack)

    val shotBack: Position = shooter.shoot(userShootablePositions, historyOfMyShotsToTheEnemy).get

    lastShot = Some(shotBack)

    Turn(outcomeOfTheLastShotFromTheEnemyToMe, shotBack)
  }

  private def userShootablePositions = Positions.createGrid(boardSize) -- historyOfMyShotsToTheEnemy.map(_._1).toSet
}