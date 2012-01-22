package org.casa.battleships

import ascii.AsciiDashboard
import strategy.FleetComposer
import org.casa.battleships.Position.pos
import strategy.positionchoice.{RandomPositionChooser, PositionChooser, UpmostAndThenLeftmostPositionChooser}
import strategy.shooting._

object Battleships {
  var settings: GameSettings = _
  var dashboard: AsciiDashboard = _

  val randomChooser: RandomPositionChooser = new RandomPositionChooser()
  val deterministicChooser = new UpmostAndThenLeftmostPositionChooser

  val randomShooter = new RandomShooter
  val deterministicShooter = new OneOneShooter

  var historyOfComputerShotsAtUser: List[(Position, ShotOutcome.Value)] = Nil

  reset

  def reset {
    settings = new GameSettings
  }

  class GameSettings( var gridSize: Int,
                      var shipSizes: List[Int],
                      var positionChooser: PositionChooser,
                      var computerShooter: Shooter){
    def this() = this(10, 5::4::3::3::2::Nil, randomChooser,
      new SequentialShooter(
        new LinesShooter(randomChooser),
        new AimAtNextToHitShooter(randomChooser),
        randomShooter)
    )

    def createComputerBoard: Board = {
      new Board(gridSize, new FleetComposer(positionChooser).create(gridSize, shipSizes).get)
    }

    def createUserBoard: Board = {
      new Board(gridSize, new FleetComposer(positionChooser).create(gridSize, shipSizes).get)
    }

    def createDashboard: AsciiDashboard = new AsciiDashboard(createComputerBoard, createUserBoard)
  }

  def quickMode = new GameSettings {
    gridSize = 4
    shipSizes = 3 :: 2 :: Nil
  }

  class GameConfigurer {
    def using(mode: GameSettings): GameConfigurer = {
      settings = mode
      this
    }

    def using(positionChooser: PositionChooser): GameConfigurer = {
      settings.positionChooser = positionChooser
      this
    }

    def using(shooter: Shooter): GameConfigurer = {
      settings.computerShooter = shooter
      this
    }
  }

  def configure: GameConfigurer = new GameConfigurer

  def start: String = {
    dashboard = settings.createDashboard
    "\n==========\nNew Game\n==========" + normalPrompt
  }

  def shoot(column: Int, row: Int): String = {
    def computerShootsUser(shootable: Set[Position]): Position = {
      settings.computerShooter.shoot(shootable, historyOfComputerShotsAtUser).get
    }

    def saveHistory(position: Position, outcome: ShotOutcome.Value){
      historyOfComputerShotsAtUser = (position, outcome) :: historyOfComputerShotsAtUser
    }

    val positionWhereUserShootsComputer: Position = pos(column, row)
    val userShootsComputerOutcome: ShotOutcome.Value = dashboard.computerBoard.shoot(positionWhereUserShootsComputer)

    val positionWhereComputerShootsUser: Position = computerShootsUser(dashboard.userBoard.shootable)
    val computerShootsUserOutcome: ShotOutcome.Value = dashboard.userBoard.shoot(positionWhereComputerShootsUser)

    saveHistory(positionWhereComputerShootsUser, computerShootsUserOutcome)

    "\nUser: " + positionWhereUserShootsComputer + " => " + userShootsComputerOutcome +
      "\nComputer: " + positionWhereComputerShootsUser.toString + " => " + computerShootsUserOutcome + "\n" + prompt
  }

  def prompt: String = {
    val endString = gameOutcome match {
      case None => normalPrompt
      case Some(outcome) => "\n" + dashboard.toAscii + outcome + start
    }
    endString
  }

  private def gameOutcome: Option[String] = {
    if (dashboard.computerBoard.areAllShipsSunk) {
      Some("\nYou win!\n")
    } else if (dashboard.userBoard.areAllShipsSunk) {
      Some("\nI win!\n")
    } else {
      None
    }
  }

  private def normalPrompt: String = {
    "\n" + dashboard.toAscii + "\nEnter your move:\n"
  }
}