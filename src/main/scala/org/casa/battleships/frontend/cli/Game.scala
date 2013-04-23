package org.casa.battleships.frontend.cli

import org.casa.battleships.frontend.{Transport, GameView}
import org.casa.battleships.{Position, ComputerPlayer}
import org.casa.battleships.Position._


class Game(val transport: Transport, val computerPlayer: ComputerPlayer) {
  var userGameView: GameView = _
  var computerGameView: GameView = _

  def shoot(userOnComputerShot: Position): String = {

    val userOnComputerShotOutcome = userGameView.shootOpponent(userOnComputerShot)

    val computerOnUserShot: Position = computerPlayer.play(computerGameView.historyOfShotsOnOpponent())

    val computerOnUserShotOutcome = computerGameView.shootOpponent(computerOnUserShot)

    "\nUser: " + userOnComputerShot + " => " + userOnComputerShotOutcome +
      "\nComputer: " + computerOnUserShot + " => " + computerOnUserShotOutcome + "\n" + prompt
  }

  def restart: String = {
    val views = GameView.createGame(transport)
    userGameView = views._1
    computerGameView = views._2

    "\n==========\nNew Game\n==========" + normalPrompt
  }

  private def prompt: String = {
    val endString = gameOutcome match {
      case None => normalPrompt
      case Some(outcome) => "\n" + userGameView.dashboard() + outcome + restart
    }
    endString
  }

  private def gameOutcome: Option[String] = {
    computerGameView.winner().map(winner => if ("Computer" == winner) "I win!\n" else "You win!\n")
  }

  private def normalPrompt: String = {
    "\n" + userGameView.dashboard + "Enter your move:\n"
  }
}