package org.casa.battleships


class Dashboard(playerIdAndBoard1: (String, Board), playerIdAndBoard2: (String, Board)) {
  val playersToBoards = Map(playerIdAndBoard1._1 -> playerIdAndBoard1._2, playerIdAndBoard2._1 -> playerIdAndBoard2._2)
  var history: List[(String, Position, ShotOutcome.Value)] = Nil

  def shoot(shooterId: String, position: Position): (ShotOutcome.Value, String) = {
    val outcome = playersToBoards(shooterId).shoot(position)
    history = (shooterId, position, outcome) :: history
    val next = (playersToBoards.keySet - shooterId).head
    (outcome, next)
  }
}