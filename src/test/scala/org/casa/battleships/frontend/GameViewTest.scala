package org.casa.battleships.frontend

import org.scalatest.{BeforeAndAfterEach, FunSuite}
import org.scalatest.matchers.ShouldMatchers
import org.mockito.Mockito._
import org.casa.battleships.ShotOutcome
import org.casa.battleships.Position.pos

class GameViewTest extends FunSuite with ShouldMatchers with BeforeAndAfterEach {
  val dashboard1: String = "dashboard1"
  val dashboard2: String = "dashboard2"

  var generalTransport: Transport = _
  var gameTransport: Transport = _

  override def beforeEach() {
    generalTransport = mock(classOf[Transport])
    gameTransport = mock(classOf[Transport])
  }

  test("Subs transport on game creation") {
    when(generalTransport.post("/game", "You,Computer")).thenReturn("1,You\n")
    when(generalTransport.sub("/game/1")).thenReturn(gameTransport)

    val (playerGameView, computerGameView) = GameView.createGame(generalTransport)

    playerGameView.transport shouldBe gameTransport
    playerGameView.playerId shouldBe "You"

    computerGameView.transport shouldBe gameTransport
    computerGameView.playerId shouldBe "Computer"
  }

  test("Creates shot and returns outcome when shooting") {
    when(gameTransport.post("/shot", "You,1,5")).thenReturn("Hit,ignored\n")

    val view = setupHumanGameView()

    view.shootOpponent(1, 5) shouldBe ShotOutcome.Hit
  }

  test("Selects the history of shots on the opponent when retrieving history") {
    when(gameTransport.get("/history")).thenReturn("" +
      "You: (1, 5) => Hit\n" +
      "Computer: (9, 9) => Water\n")

    val humanView = setupHumanGameView()
    humanView.historyOfShotsOnOpponent() shouldBe (pos(1, 5), ShotOutcome.Hit) :: Nil

    val computerView = setupComputerGameView()
    computerView.historyOfShotsOnOpponent() shouldBe (pos(9, 9), ShotOutcome.Water) :: Nil
  }

  test("Passes the dashboard for the player through") {
    when(gameTransport.get("/dashboard/You")).thenReturn(dashboard1)
    when(gameTransport.get("/dashboard/Computer")).thenReturn(dashboard2)

    val humanView = setupHumanGameView()
    humanView.dashboard() shouldBe (dashboard1)

    val computerView = setupComputerGameView()
    computerView.dashboard() shouldBe (dashboard2)
  }

  test("Gets the winner when there is one") {
    when(gameTransport.getWithStatusCode("/winner")).thenReturn((200, "You\n"))
    val view = setupHumanGameView()

    view.winner() shouldBe Some("You")
  }

  test("It doesn't get the winner when there none yet") {
    when(gameTransport.getWithStatusCode("/winner")).thenReturn((404, "ignored"))
    val view = setupHumanGameView()

    view.winner() shouldBe None
  }

  private def setupHumanGameView(): GameView = {
    setupGameViews._1
  }

  private def setupComputerGameView(): GameView = {
    setupGameViews._2
  }

  private def setupGameViews: (GameView, GameView) = {
    when(generalTransport.post("/game", "You,Computer")).thenReturn("1,You\n")
    when(generalTransport.sub("/game/1")).thenReturn(gameTransport)

    GameView.createGame(generalTransport)
  }
}