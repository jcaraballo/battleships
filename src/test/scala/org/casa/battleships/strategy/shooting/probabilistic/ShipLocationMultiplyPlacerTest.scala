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
import org.scalatest.matchers.ShouldMatchers

class ShipLocationMultiplyPlacerTest extends FunSuite with ShouldMatchers {
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

  test("All possible locations for 2 square ship in 10x10 grid") {
    val locations = placer.findAllShipLocations(2, createGrid(10))
    // 9 positions per column/row, 10 columns and 10 rows
    assertThat(locations.size, is(9 * (10 + 10)))
    val flatten: Set[Position] = locations.map(_.squares).flatten
    for (p <- flatten) {
      p.column should be >= (0)
      p.row should be >= (0)
      p.column should be <= (10)
      p.row should be <= (10)
    }
  }
}