package org.casa.battleships


class Dashboard(playerIdAndBoard1: (String, Board), playerIdAndBoard2: (String, Board)) {
  val playersToBoards = Map(playerIdAndBoard1, playerIdAndBoard2)
  var history: List[(String, Position, ShotOutcome.Value)] = Nil

  def shoot(shooterId: String, position: Position): (ShotOutcome.Value, String) = {
    val outcome = playersToBoards(shooterId).shoot(position)
    history = (shooterId, position, outcome) :: history
    val next = (playersToBoards.keySet - shooterId).head
    (outcome, next)
  }
}