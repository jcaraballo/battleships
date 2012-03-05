package org.casa.battleships.strategy.shooting.probabilistic

import org.scalatest.FunSuite
import org.casa.battleships.Position.pos
import org.junit.Assert.assertThat
import org.hamcrest.CoreMatchers.is
import collection.immutable.Set
import org.casa.battleships.Positions.createGrid
import org.casa.battleships.fleet.ShipLocation
import org.casa.battleships.Position
import testtools.Matchers.isEmpty

class ShipLocationMultiplyPlacerTest extends FunSuite {
  val placer: ShipLocationMultiplyPlacer = new ShipLocationMultiplyPlacer()

  test("No possible placement when no free space") {
    assertThat(placer.findAllShipLocations(2, Set[Position]()), isEmpty)
  }

  test("One possible placement when one place where it fits") {
    assertThat(
      placer.findAllShipLocations(2, Set(pos(1, 1), pos(2, 1))),
      is(Set(new ShipLocation(pos(1, 1), pos(2, 1))))
    )
  }

  test("All possible locations for 2 square ship in 2x2 grid") {
    assertThat(placer.findAllShipLocations(2, createGrid(2)), is(Set(

      //horizontal locations
      new ShipLocation(pos(1, 1), pos(2, 1)),
      new ShipLocation(pos(1, 2), pos(2, 2)),

      //verticals locations
      new ShipLocation(pos(1, 1), pos(1, 2)),
      new ShipLocation(pos(2, 1), pos(2, 2))

    )))
  }

  test("All possible locations for 2 square ship in 3x3 grid") {
    assertThat(placer.findAllShipLocations(2, createGrid(3)), is(Set(

      //horizontal locations
      new ShipLocation(pos(1, 1), pos(2, 1)),
      new ShipLocation(pos(2, 1), pos(3, 1)),
      new ShipLocation(pos(1, 2), pos(2, 2)),
      new ShipLocation(pos(2, 2), pos(3, 2)),
      new ShipLocation(pos(1, 3), pos(2, 3)),
      new ShipLocation(pos(2, 3), pos(3, 3)),

      //verticals locations
      new ShipLocation(pos(1, 1), pos(1, 2)),
      new ShipLocation(pos(1, 2), pos(1, 3)),
      new ShipLocation(pos(2, 1), pos(2, 2)),
      new ShipLocation(pos(2, 2), pos(2, 3)),
      new ShipLocation(pos(3, 1), pos(3, 2)),
      new ShipLocation(pos(3, 2), pos(3, 3))
    )))
  }
}