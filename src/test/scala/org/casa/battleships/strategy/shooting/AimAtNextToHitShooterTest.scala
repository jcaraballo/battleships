package org.casa.battleships.strategy.shooting

import org.scalatest.junit.JUnitSuite
import org.junit.Test
import org.mockito.Mockito._
import org.mockito.Matchers._
import org.casa.battleships.Position.pos
import org.casa.battleships.strategy.positionchoice.PositionChooser
import org.casa.battleships.Position

class AimAtNextToHitShooterTest extends JUnitSuite {
  val delegate: Shooter = mock(classOf[Shooter])
  val chooser: PositionChooser = mock(classOf[PositionChooser])

  @Test def delegatesWhenThereIsNoPreviousShot() {
    val position: Position = pos(1, 1)
    val emptyHistory: List[(Position, String)] = List[(Position, String)]()

    when(delegate.shoot(Set(position), emptyHistory)).thenReturn(Some(position))

    expect(Some(position)) {
      new AimAtNextToHitShooter(chooser)(delegate).shoot(Set(position), emptyHistory)
    }
  }

  @Test def delegatesWhenLastNonWaterWasSunk() {
    val position: Position = pos(1, 1)
    val history = (pos(3, 4), "water") :: (pos(4, 4), "sunk") :: Nil

    when(delegate.shoot(Set(position), history)).thenReturn(Some(position))

    expect(Some(position)) {
      new AimAtNextToHitShooter(chooser)(delegate).shoot(Set(position), history)
    }
  }

  @Test def delegatesWhenLastNonWaterWasHitButItsNeighboursAreNotShootable() {
    val position: Position = pos(1, 1)
    val history = (pos(3, 4), "water") :: (pos(4, 4), "sunk") :: Nil

    when(delegate.shoot(Set(position), history)).thenReturn(Some(position))
    when(chooser.choose(isA(classOf[Set[Position]]))).thenReturn(None)

    expect(Some(position)) {
      new AimAtNextToHitShooter(chooser)(delegate).shoot(Set(position), history)
    }
  }

  @Test def picksOneOfTheLastNonWaterWhenThatWasHitAndShootable() {
    val position: Position = pos(1, 1)
    val history = (pos(3, 4), "water") :: (pos(4, 4), "hit") :: Nil

    when(chooser.choose(isA(classOf[Set[Position]]))).thenReturn(Some(position))

    expect(Some(position)) {
      new AimAtNextToHitShooter(chooser)(delegate).shoot(Set(position), history)
    }

    verifyZeroInteractions(delegate)
  }
}