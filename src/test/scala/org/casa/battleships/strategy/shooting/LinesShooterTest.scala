package org.casa.battleships.strategy.shooting

import org.scalatest.junit.JUnitSuite
import org.junit.Test
import org.mockito.Mockito._
import org.mockito.Matchers._
import org.casa.battleships.Position.pos
import org.casa.battleships.strategy.positionchoice.{UpmostAndThenLeftmostPositionChooser, PositionChooser}
import org.casa.battleships.{Positions, Position}

class LinesShooterTest extends JUnitSuite {
  val delegate: Shooter = mock(classOf[Shooter])
  val chooser: PositionChooser = mock(classOf[PositionChooser])

  @Test def shootsAtContinuationOfLine(){
    when(chooser.choose(Set(pos(6, 3)))).thenReturn(Some(pos(6,3)))

    val shootables = Set(
        pos(5, 2), pos(6, 2), pos(9, 2),
        pos(5, 3), pos(6, 3),
        pos(6, 4), pos(9, 4)
    )

    val history = (pos(7, 3), "hit") :: (pos(8, 2), "water") :: (pos(8, 4), "water") :: (pos(8, 3), "hit") :: Nil

    expect(Some(pos(6, 3))){
      new LinesShooter(chooser)(delegate).shoot(shootables, history)
    }

    verifyZeroInteractions(delegate)
  }
}