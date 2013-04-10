package org.casa.battleships.frontend

import org.casa.battleships.{ShotOutcome, Position}
import org.casa.battleships.Position._

class GameView(val transport: Transport, val playerId: String) {
  def dashboard(): String = {
    transport.get("/dashboard/" + playerId)
  }

  def myFleet(): Map[Position, String] = {
    val dash: String = dashboard()

    val payload: Array[String] = dash.trim.split('\n').map {
      line =>
        val leftBorder = line.lastIndexOf("{")
        val rightBorder = line.lastIndexOf("}")
        if (leftBorder == -1 || rightBorder == -1) None else Some(line.substring(leftBorder + 1, rightBorder))
    }.flatten.map(_.zipWithIndex.filter(_._2 % 2 == 0).map(_._1).mkString)

    GameView.convertBoardPayloadToMap(payload)
  }

  def shootOpponent(column: Int, row: Int): ShotOutcome.Value = {
    val body = transport.post("/shot", playerId + "," + column + "," + row).trim
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

  def winner(): Option[String] = {
    transport.getWithStatusCode("/winner") match {
      case (200, response) => Some(response.trim)
      case (404, _) => None
      case (code, response) => throw new RuntimeException("Unexpected response " +(code, response))
    }
  }

  private def opponentsId = if ("Computer" == playerId) "You" else "Computer"
}

object GameView {
  def createGame(transport: Transport): (GameView, GameView) = {
    val result: String = transport.post("/game", "You,Computer").trim
    if (!result.endsWith(",You")) throw new RuntimeException("Expected response ending in ',You', but got: " + result)

    val gameId: Int = result.substring(0, result.length - ",You".length).toInt

    val gameTransport: Transport = transport.sub("/game/" + gameId)
    (new GameView(gameTransport, "You"), new GameView(gameTransport, "Computer"))
  }

  def convertBoardPayloadToMap(payload: Array[String]): Map[Position, String] = {
    val entries = payload.zipWithIndex.map {
      lineAndIndex => lineAndIndex._1.zipWithIndex.map {
        charAndIndex => pos(charAndIndex._2 + 1, lineAndIndex._2 + 1) -> charAndIndex._1.toString
      }
    }.flatten
    Map(entries: _*)
  }
}