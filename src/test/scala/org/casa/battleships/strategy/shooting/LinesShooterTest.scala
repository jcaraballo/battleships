package org.casa.battleships.strategy.shooting

import org.scalatest.junit.JUnitSuite
import org.junit.Test
import org.mockito.Mockito._
import org.casa.battleships.Position.pos
import org.casa.battleships.strategy.positionchoice.PositionChooser
import org.casa.battleships.ShotOutcome._

class LinesShooterTest extends JUnitSuite {
  val delegate: Shooter = mock(classOf[Shooter])
  val chooser: PositionChooser = mock(classOf[PositionChooser])

  @Test def shootsAtContinuationOfLine(){
    when(chooser.choose(Set(pos(6, 3)))).thenReturn(Some(pos(6,3)))

    val shootablePositions = Set(
        pos(5, 2), pos(6, 2), pos(9, 2),
        pos(5, 3), pos(6, 3),
        pos(6, 4), pos(9, 4)
    )

    val history = (pos(7, 3), Hit) :: (pos(8, 2), Water) :: (pos(8, 4), Water) :: (pos(8, 3), Hit) :: Nil

    expect(Some(pos(6, 3))){
      new LinesShooter(chooser)(delegate).shoot(shootablePositions, history)
    }

    verifyZeroInteractions(delegate)
  }
}