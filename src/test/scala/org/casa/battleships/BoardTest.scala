package org.casa.battleships

import org.junit.Test
import org.casa.battleships.Position.pos
import org.scalatest.junit.JUnitSuite
import org.casa.battleships.fleet._

class BoardTest extends JUnitSuite {
  val fleet: Fleet = new Fleet(
    AircraftCarrier(pos(1, 1), pos(5, 1)),
    Battleship(pos(1, 2), pos(4, 2)),
    Destroyer(pos(1, 3), pos(3, 3)),
    Submarine(pos(1, 4), pos(3, 4)),
    PatrolBoat(pos(1, 5), pos(2, 5))
  )

  @Test def createsFromFleet(){
    new Board(10, fleet)
  }

  @Test def shootReturnsWaterIfNoShipIsPlacedThere(){
    expect("water"){new Board(10, fleet).shoot(pos(10, 10))}
  } 

  @Test def shootReturnsHitIfThereIsAShipThereThatHasOtherPartsThatHaveNotBeenHit(){
    expect("hit"){new Board(10, fleet).shoot(pos(1, 1))}
  }

  @Test def shootReturnsSunkWhenThereIsAShipThereWhichAllOtherPartsHaveBeenHit(){
    val b: Board = new Board(10, fleet)
    expect("hit"){b.shoot(pos(1, 1))}
    expect("hit"){b.shoot(pos(2, 1))}
    expect("hit"){b.shoot(pos(3, 1))}
    expect("hit"){b.shoot(pos(4, 1))}
    expect("sunk"){b.shoot(pos(5, 1))}
  }

  @Test def shootableExcludesAlreadyShotSquares(){
    val all: Set[Position] = Positions.createGrid(10)
    val b: Board = new Board(10, fleet)
    expect(all){b.shootable}
    b.shoot(pos(1, 1))
    expect(all-pos(1, 1)){b.shootable}
    b.shoot(pos(2, 1))
    expect(all-pos(1, 1)-pos(2, 1)){b.shootable}
  }

  @Test def areAllShipsSunkDelegatesToFleet() {
    val sunkFleet: Fleet = new Fleet() {
      override def isSunk = true
    }
    expect(true) {
      new Board(4, sunkFleet).areAllShipsSunk
    }

    val perfectFleet: Fleet = new Fleet() {
      override def isSunk = false
    }
    expect(false) {
      new Board(4, perfectFleet).areAllShipsSunk
    }
  }
}