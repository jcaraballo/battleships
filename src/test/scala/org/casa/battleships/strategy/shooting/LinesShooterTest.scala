package org.casa.battleships.strategy.shooting

import org.mockito.Mockito._
import org.casa.battleships.Position.pos
import org.casa.battleships.strategy.positionchoice.PositionChooser
import org.casa.battleships.ShotOutcome._
import org.casa.battleships.Position
import org.junit.Assert.assertThat
import org.hamcrest.CoreMatchers.is
import org.scalatest.FunSuite

class LinesShooterTest extends FunSuite {
  val chooser: PositionChooser = mock(classOf[PositionChooser])

  test("shoots at continuation of line"){
    when(chooser.choose(Set(pos(6, 3)))).thenReturn(Some(pos(6,3)))

    val shootablePositions = Set(
        pos(5, 2), pos(6, 2), pos(9, 2),
        pos(5, 3), pos(6, 3),
        pos(6, 4), pos(9, 4)
    )

    val history = (pos(7, 3), Hit) :: (pos(8, 2), Water) :: (pos(8, 4), Water) :: (pos(8, 3), Hit) :: Nil

    expectResult(Some(pos(6, 3))){
      new LinesShooter(chooser).shoot(shootablePositions, history)
    }
  }

  test("shoots at continuation of line even further than immediate neighbours"){
    val hits = Set(pos(2, 2), pos(3, 2), pos(4, 2), pos(5, 2), pos(6, 2))

    val selectedTargets: Set[Position] = new LinesShooter(chooser).findPossibleEndsOfLinePassingByCenter(hits, pos(4, 2))
    assertThat(selectedTargets, is(Set(pos(1, 2), pos(7, 2))))
  }

  test("considers both horizontal and vertical continuations when both are applicable"){
    val hits = Set(pos(3, 2),
        pos(2, 3), pos(3, 3), pos(4, 3),
                   pos(3, 4))
    val chosenTarget = new LinesShooter(chooser).findPossibleEndsOfLinePassingByCenter(hits, pos(3, 3))
    assertThat(chosenTarget, is(Set(pos(3, 1), pos(1, 3), pos(5, 3), pos(3, 5))))
  }
}