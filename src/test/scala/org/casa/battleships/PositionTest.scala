package org.casa.battleships

import org.junit.Test
import org.casa.battleships.Position.pos
import org.scalatest.junit.JUnitSuite
import org.casa.battleships.fleet._

class PositionTest extends JUnitSuite {
  @Test def createsNeighboringPositions(){
    expect(pos(1,1)){pos(2,1).left}
    expect(pos(2,1)){pos(1,1).right}
    expect(pos(1,1)){pos(1,2).up}
    expect(pos(1, 2)){pos(1,1).down}
  }
}