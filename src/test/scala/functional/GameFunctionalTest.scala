package functional

import org.scalatest.FunSuite
import org.casa.battleships.strategy.positionchoice.UpmostAndThenLeftmostPositionChooser
import org.casa.battleships.strategy.shooting.OneOneShooter
import org.casa.battleships.Position.pos
import org.casa.battleships.ShotOutcome.{Water, Hit}
import org.junit.Assert.assertThat
import org.hamcrest.CoreMatchers.is
import org.hamcrest.CoreMatchers.anyOf
import org.casa.battleships.strategy.shooting.Shooters.bestShooter
import collection.immutable.Set
import org.casa.battleships.{Positions, Turn, ComputerPlayer}

class GameFunctionalTest extends FunSuite {
  test("Plays game where user starts") {
    val computerPlayer = new ComputerPlayer(new UpmostAndThenLeftmostPositionChooser, new OneOneShooter, 10, 5 :: 4 :: 3 :: 3 :: 2 :: Nil)

    assertThat(computerPlayer.playFirstTurn(pos(10, 10)), is(Turn(Water, pos(1, 1))))

    assertThat(computerPlayer.play(Turn(Hit, pos(1, 1))), is(Turn(Hit, pos(1, 1))))
  }

  test("Finds immaculate ship when there is only one possible place for it") {
    val computerPlayer = new ComputerPlayer(new UpmostAndThenLeftmostPositionChooser, bestShooter, 10, 5 :: Nil)

    val spaceForShip = Set(pos(1, 1), pos(2, 1), pos(3, 1), pos(4, 1), pos(5, 1))
    computerPlayer.historyOfMyShotsAtTheEnemy = (Positions.createGrid(10) -- spaceForShip).map((_, Water)).toList

    assertThat(computerPlayer.playFirstTurn(pos(1, 1)).shotBack, anyOf(is(pos(1, 1)), is(pos(2, 1)), is(pos(3, 1)), is(pos(4, 1)), is(pos(5, 1))))
  }
}