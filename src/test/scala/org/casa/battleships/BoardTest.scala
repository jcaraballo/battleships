package org.casa.battleships

import org.junit.Test
import org.casa.battleships.Position.pos
import org.scalatest.junit.JUnitSuite
import org.casa.battleships.fleet.Ship.immaculateShip
import org.casa.battleships.fleet.Fleet
import org.mockito.Mockito._
import org.junit.Assert.assertThat
import org.hamcrest.CoreMatchers.is

class BoardTest extends JUnitSuite {
  val someFleet: Fleet = new Fleet(
    immaculateShip(pos(1, 1), pos(5, 1)),
    immaculateShip(pos(1, 2), pos(4, 2)),
    immaculateShip(pos(1, 3), pos(3, 3)),
    immaculateShip(pos(1, 4), pos(3, 4)),
    immaculateShip(pos(1, 5), pos(2, 5))
  )

  @Test def shootDelegatesToFleetAndUpdatesShotPositions() {
    val position: Position = mock(classOf[Position])
    val outcome: ShotOutcome.Value = ShotOutcome.Hit
    val fleet: Fleet = mock(classOf[Fleet])
    val updatedFleet: Fleet = mock(classOf[Fleet])

    when(fleet.shootAt(position)).thenReturn((updatedFleet, outcome))

    val board: Board = new Board(10, fleet)
    assertThat(board.shoot(position), is(outcome))
    assertThat(board.shotPositions, is(Set(position)))
  }

  @Test def shootableExcludesAlreadyShotSquares() {
    val all: Set[Position] = Positions.createGrid(10)

    val board: Board = new Board(10, someFleet)
    assertThat(board.shootable, is(all))

    board.shoot(pos(1, 1))
    assertThat(board.shootable, is(all - pos(1, 1)))

    board.shoot(pos(2, 1))
    assertThat(board.shootable, is(all - pos(1, 1) - pos(2, 1)))
  }

  @Test def areAllShipsSunkDelegatesToFleet() {
    val sunkFleet: Fleet = new Fleet() {
      override def isSunk = true
    }
    assertThat(new Board(4, sunkFleet).areAllShipsSunk, is(true))

    val perfectFleet: Fleet = new Fleet() {
      override def isSunk = false
    }
    assertThat(new Board(4, perfectFleet).areAllShipsSunk, is(false))
  }
}