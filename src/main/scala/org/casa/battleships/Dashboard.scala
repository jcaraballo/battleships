package org.casa.battleships


// TODO: The board is mutable. Either change that or make a copy of the parameter.
class Dashboard(playerIdAndBoard1: (String, Board), playerIdAndBoard2: (String, Board)) {
  val playersToBoards = Map(playerIdAndBoard1, playerIdAndBoard2)
  var history: List[(String, Position, ShotOutcome.Value)] = Nil

  def shoot(shooterId: String, position: Position): (ShotOutcome.Value, String) = {
    val victim = opponent(shooterId)
    val outcome = playersToBoards(victim).shoot(position)
    history = (shooterId, position, outcome) :: history
    (outcome, victim)
  }

  def opponent(shooterId: String): String = {
    (playersToBoards.keySet - shooterId).head
  }
}