package org.casa.battleships.strategy

import org.junit.Test
import org.casa.battleships.Position.pos
import org.casa.battleships.strategy.ShipPlacer._
import org.scalatest.junit.JUnitSuite
import org.casa.battleships.Position
import org.casa.battleships.strategy.positionchoice.UpmostAndThenLeftmostPositionChooser

class ShipPlacerTest extends JUnitSuite {
  val chooser = new UpmostAndThenLeftmostPositionChooser

  @Test def possibleContinuationsAreUpRightDownLeftWhenThereIsOnlyOneChosenSquare {
    val r: Position = pos(2, 2)
    expect(Set(r.up, r.right, r.down, r.left)) {
      calculatePossibleContinuations(Set(r))
    }
  }

  @Test def possibleContinuationsAreNextLeftAndRightWhenThereAreSeveralHorizontallyChosen {
    expect(Set(pos(1, 2), pos(4, 2))) {
      calculatePossibleContinuations(Set(pos(2, 2), pos(3, 2)))
    }
  }

  @Test def possibleContinuationsAreNextUpAndDownWhenThereAreSeveralVerticallyChosen {
    expect(Set(pos(2, 1), pos(2, 4))) {
      calculatePossibleContinuations(Set(pos(2, 2), pos(2, 3)))
    }
  }

  @Test def takesAllWhenThatIsTheOnlyWay() {
    expect(Some(Set(pos(2, 1), pos(3, 1)))) {
      new ShipPlacer(chooser).place(2, Set(pos(2, 1), pos(3, 1)))
    }
  }

  @Test def failsToPlaceShipWhenThereIsNoSpace() {
    expect(None) {
      new ShipPlacer(chooser).place(2, Set(pos(2, 1)))
    }
    expect(None) {
      new ShipPlacer(chooser).place(2, Set[Position]())
    }
  }
}