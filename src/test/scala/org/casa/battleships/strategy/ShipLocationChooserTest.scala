package org.casa.battleships.strategy

import org.junit.Test
import org.casa.battleships.Position.pos
import org.casa.battleships.strategy.ShipLocationChooser._
import org.scalatest.junit.JUnitSuite
import org.casa.battleships.Position
import org.casa.battleships.strategy.positionchoice.UpmostAndThenLeftmostPositionChooser
import org.casa.battleships.fleet.ShipLocation

class ShipLocationChooserTest extends JUnitSuite {
  val chooser = new UpmostAndThenLeftmostPositionChooser

  @Test def possibleContinuationsAreUpRightDownLeftWhenThereIsOnlyOneChosenSquare {
    val r: Position = pos(2, 2)
    expectResult(Set(r.up, r.right, r.down, r.left)) {
      calculatePossibleContinuations(Set(r))
    }
  }

  @Test def possibleContinuationsAreNextLeftAndRightWhenThereAreSeveralHorizontallyChosen {
    expectResult(Set(pos(1, 2), pos(4, 2))) {
      calculatePossibleContinuations(Set(pos(2, 2), pos(3, 2)))
    }
  }

  @Test def possibleContinuationsAreNextUpAndDownWhenThereAreSeveralVerticallyChosen {
    expectResult(Set(pos(2, 1), pos(2, 4))) {
      calculatePossibleContinuations(Set(pos(2, 2), pos(2, 3)))
    }
  }

  @Test def takesAllWhenThatIsTheOnlyWay() {
    expectResult(Some(new ShipLocation(Set(pos(2, 1), pos(3, 1))))) {
      new ShipLocationChooser(chooser).place(2, Set(pos(2, 1), pos(3, 1)))
    }
  }

  @Test def failsToPlaceShipWhenThereIsNoSpace() {
    expectResult(None) {
      new ShipLocationChooser(chooser).place(2, Set(pos(2, 1)))
    }
    expectResult(None) {
      new ShipLocationChooser(chooser).place(2, Set[Position]())
    }
  }
}