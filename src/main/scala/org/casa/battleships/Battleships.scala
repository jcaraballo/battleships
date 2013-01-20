package org.casa.battleships

import ascii.AsciiDashboard
import fleet.Bag
import strategy.FleetComposer
import org.casa.battleships.Position.pos
import strategy.positionchoice.{RandomPositionChooser, PositionChooser, UpmostAndThenLeftmostPositionChooser}
import strategy.shooting._
import org.casa.battleships.strategy.shooting.Shooters.bestShooter

object Battleships {
  val defaultShipSizes = Bag(5, 4, 3, 3, 2)
  var settings: GameSettings = _
  var dashboard: AsciiDashboard = _

  val randomChooser: RandomPositionChooser = new RandomPositionChooser()
  val deterministicChooser = new UpmostAndThenLeftmostPositionChooser

  val randomShooter = new ArbitraryShooter(randomChooser)
  val deterministicShooter = new OneOneShooter

  var computerPlayer: ComputerPlayer = _
  var outcomeOfTheLastComputerShotAtTheUser: Option[ShotOutcome.Value] = None

  reset

  def reset {
    settings = new GameSettings
  }

  class GameSettings(var gridSize: Int,
                     var shipSizes: Bag[Int],
                     var positionChooser: PositionChooser,
                     var computerShooter: Shooter) {
    //    def this() = this(10, defaultShipSizes, randomChooser, newBestShooter(randomChooser, ActorSystem("MySystem"), defaultShipSizes, 5 seconds))
    def this() = this(10, defaultShipSizes, randomChooser, bestShooter(randomChooser))

    def createDashboard(computerBoard: Board): AsciiDashboard = {
      val userBoard = new Board(gridSize, new FleetComposer(positionChooser).create(gridSize, shipSizes.toList).get)
      new AsciiDashboard("Computer" -> computerBoard, "You" -> userBoard)
    }
  }

  def quickMode = new GameSettings {
    gridSize = 4
    shipSizes = Bag(3, 2)
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
    computerPlayer = new ComputerPlayer(settings.positionChooser, settings.computerShooter, settings.gridSize, settings.shipSizes.toList)
    outcomeOfTheLastComputerShotAtTheUser = None

    dashboard = settings.createDashboard(computerPlayer.board)
    "\n==========\nNew Game\n==========" + normalPrompt
  }

  def shoot(column: Int, row: Int): String = {
    val positionWhereUserShootsComputer: Position = pos(column, row)

    val turn: Turn = outcomeOfTheLastComputerShotAtTheUser match {
      case None => computerPlayer.playFirstTurn(positionWhereUserShootsComputer)
      case Some(outcome) => computerPlayer.play(Turn(outcome, positionWhereUserShootsComputer))
    }

    val positionWhereComputerShootsUser: Position = turn.shotBack
    val computerShootsUserOutcome: ShotOutcome.Value = dashboard.playerAndVisibleBoard._2.shoot(positionWhereComputerShootsUser)

    outcomeOfTheLastComputerShotAtTheUser = Some(computerShootsUserOutcome)

    "\nUser: " + positionWhereUserShootsComputer + " => " + turn.lastShotOutcome +
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
    if (dashboard.playerAndHiddenBoard._2.areAllShipsSunk) {
      Some("\nYou win!\n")
    } else if (dashboard.playerAndVisibleBoard._2.areAllShipsSunk) {
      Some("\nI win!\n")
    } else {
      None
    }
  }

  private def normalPrompt: String = {
    "\n" + dashboard.toAscii + "\nEnter your move:\n"
  }
}