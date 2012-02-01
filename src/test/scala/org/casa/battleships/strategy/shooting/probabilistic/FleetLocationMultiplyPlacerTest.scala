package org.casa.battleships.strategy.shooting.probabilistic

import org.scalatest.FunSuite
import org.junit.Assert.assertThat
import org.hamcrest.CoreMatchers.is
import FleetLocationMultiplyPlacer.findAllValidLocations
import org.casa.battleships.Position._
import org.casa.battleships.Positions
import org.casa.battleships.fleet.{ShipLocation, FleetLocation}
import testtools.fixtures.Builders.createHistoryOfWater
import testtools.fixtures.Examples.someFleetConfiguration

class FleetLocationMultiplyPlacerTest extends FunSuite {

  test("No possible location when no available space") {
    assertThat(findAllValidLocations(someFleetConfiguration, Set()), is(Set[FleetLocation]()))
  }

  test("Empty fleet when no ships to place") {
    assertThat(findAllValidLocations(Nil, Positions.createGrid(10)), is(Set(new FleetLocation(Set[ShipLocation]()))))
  }

  test("All possible fleets for 2 ships of 2 squares in grid") {
    val horizontalShips = Set(
      new ShipLocation(pos(1, 1), pos(2, 1)),
      new ShipLocation(pos(1, 2), pos(2, 2)))

    val verticalShips = Set(
      new ShipLocation(pos(1, 1), pos(1, 2)),
      new ShipLocation(pos(2, 1), pos(2, 2))
    )

    assertThat(findAllValidLocations(2 :: 2 :: Nil, Positions.createGrid(2)), is(Set(
      new FleetLocation(horizontalShips), new FleetLocation(verticalShips)
    )))
  }

  test("Finds unique fleet when there is only one place where it fits"){
    val waterPositions = createHistoryOfWater("""
      1 2 3 4 5 6 7 8 9 0
      ~~~~~~~~~~~~~~~~~~~
    1{  ·   ·   ·   ·   ·}1
    2{·   ·   ·   ·   ·  }2
    3{  ·   ·   ·   ·   ·}3
    4{·   ·   ·   ·   ·  }4
    5{  ·   ·   ·   ·   ·}5
    6{·   ·   ·   ·   ·  }6
    7{  ·   ·   ·   ·   ·}7
    8{·   ·   ·   ·   ·  }8
    9{· · · · · · · · · ·}9
    0{          · · · · ·}0
      ~~~~~~~~~~~~~~~~~~~
      1 2 3 4 5 6 7 8 9 0
    """).map(_._1)

    val available = Positions.createGrid(10) -- waterPositions

    assertThat(findAllValidLocations(5 :: Nil, available), is(Set(
      new FleetLocation(Set(ShipLocation(Set(pos(1, 10), pos(2, 10), pos(3, 10), pos(4, 10), pos(5, 10)))))
    )))
  }
}