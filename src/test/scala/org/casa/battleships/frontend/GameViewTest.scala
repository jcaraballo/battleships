package org.casa.battleships.frontend

import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers
import org.mockito.Mockito._
import org.casa.battleships.ShotOutcome
import org.casa.battleships.Position.pos

class GameViewTest extends FunSuite with ShouldMatchers {
  val dashboard1: String = "dashboard1"
  val dashboard2: String = "dashboard2"

  test("Plays through transport") {
    val generalTransport: Transport = mock(classOf[Transport])
    val gameTransport: Transport = mock(classOf[Transport])

    when(generalTransport.post("/game", "You,Computer")).thenReturn("1,You")
    when(generalTransport.sub("/game/1")).thenReturn(gameTransport)
    when(gameTransport.post("/shot", "You,1,5")).thenReturn("Hit,Computer")
    when(gameTransport.post("/shot", "Computer,9,9")).thenReturn("Water,You")
    when(gameTransport.get("/history")).thenReturn("" +
      "You: (1, 5) => Hit\n" +
      "Computer: (9, 9) => Water")
    when(gameTransport.get("/dashboard/You")).thenReturn(dashboard1)
    when(gameTransport.get("/dashboard/Computer")).thenReturn(dashboard2)

    val (playerGameView, computerGameView) = GameView.createGame(generalTransport)

    playerGameView.shootOpponent(1, 5) shouldBe ShotOutcome.Hit
    computerGameView.shootOpponent(9, 9) shouldBe ShotOutcome.Water

    playerGameView.historyOfShotsOnOpponent() shouldBe (pos(1, 5), ShotOutcome.Hit) :: Nil
    computerGameView.historyOfShotsOnOpponent() shouldBe (pos(9, 9), ShotOutcome.Water) :: Nil

    playerGameView.dashboard() shouldBe (dashboard1)
    computerGameView.dashboard() shouldBe (dashboard2)
  }
}