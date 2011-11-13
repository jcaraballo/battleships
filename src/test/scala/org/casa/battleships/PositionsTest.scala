package org.casa.battleships

import org.junit.Test
import org.scalatest.junit.JUnitSuite
import org.casa.battleships.Position.pos
import org.casa.battleships.Positions._

class PositionsTest extends JUnitSuite{
  @Test def leftmostGivesAnElementInTheLowestColumn(){
    expect(pos(1, 1)){
      leftmost(Set(pos(1, 1), pos(2, 1), pos(3, 1)))
    }
  }

  @Test def rightmostGivesAnElementInTheHighestColumn(){
    expect(pos(3, 1)){
      rightmost(Set(pos(1, 1), pos(2, 1), pos(3, 1)))
    }
  }

  @Test def upmostGivesAnElementInTheLowestRow(){
    expect(pos(1, 1)){
      upmost(Set(pos(1, 1), pos(1, 2), pos(1, 3)))
    }
  }

  @Test def downmostGivesAnElementInTheHighestRow(){
    expect(pos(1, 3)){
      downmost(Set(pos(1, 1), pos(1, 2), pos(1, 3)))
    }
  }

  @Test def createsSquareGrid() {
    expect(Set(
      pos(1, 1), pos(2, 1),
      pos(1, 2), pos(2, 2)
    )) {
      Positions.createGrid(2)
    }
  }
}