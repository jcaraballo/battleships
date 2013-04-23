package org.casa.battleships.frontend

import org.casa.battleships.strategy.shooting.{Shooters, OneOneShooter, Shooter}
import org.casa.battleships.strategy.positionchoice.RandomPositionChooser
import org.casa.battleships.ComputerPlayer


class UiArguments(val args: Array[String]) {
  def transport: Transport = {
    val apiServerUrl = retrieve(1) getOrElse "http://localhost:8080"
    new Transport(apiServerUrl)
  }

  def computerPlayer: ComputerPlayer = {
    val computerShooter: Shooter = retrieve(2) match {
      case Some("deterministicShooter") => new OneOneShooter
      case _ => (Shooters.bestShooter(new RandomPositionChooser))
    }
    new ComputerPlayer(computerShooter, 10)
  }

  private def retrieve(index: Int): Option[String] = {
    if (args.length > index && !args(index).isEmpty) Some(args(index)) else None
  }
}