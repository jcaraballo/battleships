package org.casa.battleships.strategy

import org.scalatest.FunSuite
import positionchoice.PositionChooser
import ShipLocationMultiplyPlacer.findAllShipLocations
import org.casa.battleships.Position.pos
import org.junit.Assert.assertThat
import org.hamcrest.CoreMatchers.is
import collection.immutable.Set
import org.casa.battleships.Positions.createGrid
import org.casa.battleships.ascii.BoardPrinters
import org.casa.battleships.fleet.{Fleet, Ship, ShipLocation}
import org.casa.battleships.{Board, Position}
import testtools.Matchers.isEmpty

class ShipLocationMultiplyPlacerTest extends FunSuite {
  test("No possible placement when no free space") {
    assertThat(findAllShipLocations(2, Set[Position]()), isEmpty)
  }

  test("One possible placement when one place where it fits") {
    assertThat(
      findAllShipLocations(2, Set(pos(1, 1), pos(2, 1))),
      is(Set(new ShipLocation(pos(1, 1), pos(2, 1))))
    )
  }

  test("All possible locations for 2 square ship in 2x2 grid") {
    assertThat(findAllShipLocations(2, createGrid(2)), is(Set(

      //horizontal locations
      new ShipLocation(pos(1, 1), pos(2, 1)),
      new ShipLocation(pos(1, 2), pos(2, 2)),

      //verticals locations
      new ShipLocation(pos(1, 1), pos(1, 2)),
      new ShipLocation(pos(2, 1), pos(2, 2))

    )))
  }

  test("All possible locations for 2 square ship in 3x3 grid") {
    assertThat(findAllShipLocations(2, createGrid(3)), is(Set(

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
  
//  // Performance comparison:
//  //     findAllShipLocations(2, grid).headOption is around 150 times slower than
//  //     new ShipLocationChooser(firstChooser).place(2, grid)
//  test("More complicated case"){
//    val gridSize = 10
//    val grid = createGrid(gridSize)
//
//    val start1 = System.currentTimeMillis()
//    val aLocationFromAll: Option[ShipLocation] = (1 to 10000).map(i =>
//      findAllShipLocations(2, grid).headOption
//    ).head
//    val end1 = System.currentTimeMillis()
//    println("aLocationFromAll in " + (end1 - start1) + " millis:\n")
//    printLocation(gridSize, aLocationFromAll.get)
//    println("\n\n")
//
//    val start2 = System.currentTimeMillis()
//    val aLocationDirect: Option[ShipLocation] = (1 to 10000).map(i =>
//            new ShipLocationChooser(firstChooser).place(2, grid)
//        ).head
//    val end2 = System.currentTimeMillis()
//    println("aLocationFromAll in " + (end2 - start2) + " millis:\n")
//    printLocation(gridSize, aLocationDirect.get)
//    println("\n\n")
//  }
//
//  private def printLocation(gridSize: Int, location: ShipLocation) {
//    val board = new Board(gridSize, new Fleet(new Ship(location, Set())))
//    println(BoardPrinters.createForUser.toAsciiStrings(board).mkString("\n"))
//  }
//
//  private def firstChooser = new PositionChooser {
//    def choose(positions: Set[Position]): Option[Position] = positions.headOption
//  }
}