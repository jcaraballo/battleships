package org.casa.battleships.strategy.shooting

import org.scalatest.junit.JUnitSuite
import org.junit.Test
import org.mockito.Mockito._
import org.mockito.Matchers._
import org.casa.battleships.Position.pos
import org.casa.battleships.strategy.positionchoice.PositionChooser
import org.casa.battleships.{ShotOutcome, Position}
import org.casa.battleships.ShotOutcome._

class AimAtNextToHitShooterTest extends JUnitSuite {
  val chooser: PositionChooser = mock(classOf[PositionChooser])

  @Test def failsWhenThereIsNoPreviousShot() {
    val position: Position = pos(1, 1)
    val emptyHistory: List[(Position, ShotOutcome.Value)] = Nil

    expect(None) {
      new AimAtNextToHitShooter(chooser).shoot(Set(position), emptyHistory)
    }
  }

  @Test def failsWhenLastNonWaterWasSunk() {
    val position: Position = pos(1, 1)
    val history = (pos(3, 4), Water) :: (pos(4, 4), Sunk) :: Nil

    expect(None) {
      new AimAtNextToHitShooter(chooser).shoot(Set(position), history)
    }
  }

  @Test def failsWhenLastNonWaterWasHitButItsNeighboursAreNotShootable() {
    val position: Position = pos(1, 1)
    val history = (pos(3, 4), Water) :: (pos(4, 4), Sunk) :: Nil

    when(chooser.choose(isA(classOf[Set[Position]]))).thenReturn(None)

    expect(None) {
      new AimAtNextToHitShooter(chooser).shoot(Set(position), history)
    }
  }

  @Test def picksOneOfTheLastNonWaterWhenThatWasHitAndShootable() {
    val position: Position = pos(1, 1)
    val history = (pos(3, 4), Water) :: (pos(4, 4), Hit) :: Nil

    when(chooser.choose(isA(classOf[Set[Position]]))).thenReturn(Some(position))

    expect(Some(position)) {
      new AimAtNextToHitShooter(chooser).shoot(Set(position), history)
    }
  }
}