package org.casa.battleships

import org.junit.Test
import org.casa.battleships.Position.pos
import org.scalatest.junit.JUnitSuite
import org.casa.battleships.fleet._

class PositionTest extends JUnitSuite {
  @Test def createsNeighboringPositions(){
    expectResult(pos(1,1)){pos(2,1).left}
    expectResult(pos(2,1)){pos(1,1).right}
    expectResult(pos(1,1)){pos(1,2).up}
    expectResult(pos(1, 2)){pos(1,1).down}
  }
}