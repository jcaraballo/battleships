package org.casa.battleships

import fleet.Bag
import frontend.{Transport, GameView}
import org.casa.battleships.Position.pos
import strategy.positionchoice.{RandomPositionChooser, PositionChooser, UpmostAndThenLeftmostPositionChooser}
import strategy.shooting._
import org.casa.battleships.strategy.shooting.Shooters.bestShooter

object Battleships {
  val defaultShipSizes = Bag(5, 4, 3, 3, 2)
  var settings: GameSettings = _

  val randomChooser: RandomPositionChooser = new RandomPositionChooser()
  val deterministicChooser = new UpmostAndThenLeftmostPositionChooser

  val randomShooter = new ArbitraryShooter(randomChooser)
  val deterministicShooter = new OneOneShooter

  var playerGameView: GameView = _
  var computerGameView: GameView = _

  reset

  def reset {
    settings = new GameSettings
  }

  class GameSettings(var gridSize: Int,
                     var shipSizes: Bag[Int],
                     var positionChooser: PositionChooser,
                     var computerShooter: Shooter,
                     var transport: Transport) {


    //    def this() = this(10, defaultShipSizes, randomChooser, newBestShooter(randomChooser, ActorSystem("MySystem"), defaultShipSizes, 5 seconds))
    def this() = this(10, defaultShipSizes, randomChooser, bestShooter(randomChooser), new Transport("http://localhost:8080"))

    def computerPlayer: ComputerPlayer = {
      new ComputerPlayer(computerShooter, gridSize)
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

    def using(shooter: Shooter): GameConfigurer = {
      settings.computerShooter = shooter
      this
    }

    def using(transport: Transport): GameConfigurer = {
      settings.transport = transport
      this
    }
  }

  def configure: GameConfigurer = new GameConfigurer

  def start: String = {
    val views: (GameView, GameView) = GameView.createGame(settings.transport)
    playerGameView = views._1
    computerGameView = views._2

    "\n==========\nNew Game\n==========" + normalPrompt
  }

  def shoot(column: Int, row: Int): String = {

    val humanOnComputerShotOutcome = playerGameView.shootOpponent(column, row)

    val computerOnHumanShot: Position = settings.computerPlayer.play(computerGameView.historyOfShotsOnOpponent())

    val computerOnHumanShotOutcome = computerGameView.shootOpponent(computerOnHumanShot.column, computerOnHumanShot.row)

    "\nUser: " + pos(column, row) + " => " + humanOnComputerShotOutcome +
      "\nComputer: " + computerOnHumanShot + " => " + computerOnHumanShotOutcome + "\n" + prompt
  }

  def prompt: String = {
    val endString = gameOutcome match {
      case None => normalPrompt
      case Some(outcome) => "\n" + playerGameView.dashboard() + outcome + start
    }
    endString
  }

  private def gameOutcome: Option[String] = {
    computerGameView.winner().map(winner => if ("Computer" == winner) "I win!\n" else "You win!\n")
  }

  private def normalPrompt: String = {
    "\n" + playerGameView.dashboard + "Enter your move:\n"
  }

  def st{println(start)}
  def s(column: Int, row: Int){println(shoot(column, row))}
}