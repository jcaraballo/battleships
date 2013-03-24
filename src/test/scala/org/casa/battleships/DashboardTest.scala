package org.casa.battleships

import fleet.Fleet
import fleet.Ship._
import org.scalatest.FunSuite
import org.casa.battleships.Position._
import org.casa.battleships.ShotOutcome.Hit
import org.scalatest.matchers.ShouldMatchers


class DashboardTest extends FunSuite with ShouldMatchers {
  val player1 = "player1"
  val player2 = "player2"

  def anImmaculateBoard: Board = new Board(4, new Fleet(
    immaculateShip(pos(1, 1), pos(3, 1)),
    immaculateShip(pos(2, 2), pos(2, 3))
  ))

  test("A player shoots at a position on the opponent's board") {
    val dashboard = new Dashboard(player1 -> anImmaculateBoard, player2 -> anImmaculateBoard)

    val (outcome, nextPlayer) = dashboard.shoot(player1, pos(1, 1))
    outcome shouldBe Hit
    nextPlayer shouldBe player2

    dashboard.playersToBoards.get(player1).get shouldBe anImmaculateBoard
    dashboard.playersToBoards.get(player2).get.shotPositions shouldBe Set(pos(1, 1))
  }
}