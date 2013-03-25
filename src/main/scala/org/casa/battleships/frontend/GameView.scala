package org.casa.battleships.frontend

import org.casa.battleships.{ShotOutcome, Position}
import org.casa.battleships.Position._

class GameView(val transport: Transport, val playerId: String) {
  def dashboard(): String = {
    transport.get("/dashboard/" + playerId)
  }

  def shootOpponent(column: Int, row: Int): ShotOutcome.Value = {
    val body = transport.post("/shot", playerId + "," + column + "," + row)
    if (!body.contains(",")) throw new IllegalArgumentException

    val parts: Array[String] = body.split(",")
    if (parts.size != 2) throw new IllegalArgumentException

    ShotOutcome.LookUp.fromString(parts(0))
  }


  def historyOfShotsOnOpponent(): List[(Position, ShotOutcome.Value)] = {
    val history: Array[String] = transport.get("/history").trim.split("\n")
    val opponentsPrefix: String = playerId + ": "
    val opponentsHistory: List[(Position, ShotOutcome.Value)] = history.toList.filter(_.startsWith(opponentsPrefix)).map(s => {
      val split: Array[String] = s.substring(opponentsPrefix.length).split(" => ")
      val shotPart: String = split(0)
      val coordinates: Array[String] = shotPart.substring(1, shotPart.length - 1).split(", ")
      val shot: Position = pos(coordinates(0).toInt, coordinates(1).toInt)
      val outcome: ShotOutcome.Value = ShotOutcome.LookUp.fromString(split(1))
      (shot, outcome)
    }).toList
    opponentsHistory
  }

  private def opponentsId = if ("Computer" == playerId) "You" else "Computer"
}

object GameView {
  def createGame(transport: Transport): (GameView, GameView) = {
    val result: String = transport.post("/game", "You,Computer")
    if (!result.endsWith(",You")) throw new RuntimeException("Expected response ending in ',You', but got: " + result)

    val gameId: Int = result.substring(0, result.length - ",You".length).toInt

    val gameTransport: Transport = transport.sub("/game/" + gameId)
    (new GameView(gameTransport, "You"), new GameView(gameTransport, "Computer"))
  }
}