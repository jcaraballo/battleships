package org.casa.battleships.strategy.shooting

import org.scalatest.junit.JUnitSuite
import org.junit.Test
import org.mockito.Mockito._
import org.casa.battleships.Position.pos
import org.casa.battleships.strategy.positionchoice.PositionChooser
import org.casa.battleships.ShotOutcome._
import org.casa.battleships.Position
import org.casa.battleships.Positions.createGrid
import org.junit.Assert.assertThat
import org.hamcrest.CoreMatchers.is

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
  
  @Test def shootsAtContinuationOfLineEvenFurtherThanImmediateNeighbours(){
    val hits = Set(pos(2, 2), pos(3, 2), pos(4, 2), pos(5, 2), pos(6, 2))
    val shootable = createGrid(10) -- hits

    when(chooser.choose(Set(pos(1, 2), pos(7, 2)))).thenReturn(Some(pos(1, 2)))

    val chosenTarget = new LinesShooter(chooser)(delegate).findLinePassingByCenter(shootable, hits, pos(4, 2))
    assertThat(chosenTarget, is[Option[Position]](Some(pos(1, 2))))
  }

  @Test def considersBothHorizontalAndVerticalContinuationsWhenBothAreApplicable(){
    val hits = Set(pos(3, 2),
        pos(2, 3), pos(3, 3), pos(4, 3),
                   pos(3, 4))
    val shootable = createGrid(10) -- hits

    when(chooser.choose(Set(pos(3, 1), pos(1, 3), pos(5, 3), pos(3, 5)))).thenReturn(Some(pos(3, 1)))

    val chosenTarget = new LinesShooter(chooser)(delegate).findLinePassingByCenter(shootable, hits, pos(3, 3))
    assertThat(chosenTarget, is[Option[Position]](Some(pos(3, 1))))
  }
}