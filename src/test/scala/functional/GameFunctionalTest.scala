package functional

import org.scalatest.FunSuite
import org.casa.battleships.strategy.positionchoice.UpmostAndThenLeftmostPositionChooser
import org.casa.battleships.strategy.shooting.OneOneShooter
import org.casa.battleships.Position.pos
import org.casa.battleships.ShotOutcome.{Water, Hit}
import org.junit.Assert.assertThat
import org.hamcrest.CoreMatchers.is
import org.casa.battleships.{Turn, Game}

class GameFunctionalTest extends FunSuite {
  test("Extra"){
    val game = new Game(10, 5::4::3::3::2::Nil, new UpmostAndThenLeftmostPositionChooser, new OneOneShooter)

    assertThat(game.playFirstTurn(pos(10, 10)), is(Turn(Water, pos(1, 1))))

    assertThat(game.play(Turn(Hit, pos(1, 1))), is(Turn(Hit, pos(1, 1))))
  }
}