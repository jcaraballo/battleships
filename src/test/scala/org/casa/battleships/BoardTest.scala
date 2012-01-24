package org.casa.battleships

import org.casa.battleships.Position.pos
import org.casa.battleships.fleet._
import org.mockito.Mockito._
import org.junit.Assert.assertThat
import org.hamcrest.CoreMatchers.is
import org.scalatest.FunSuite
import testtools.fixtures.Examples.someFleet

class BoardTest extends FunSuite {

  test("shoot delegates to fleet and updates shotPositions") {
    val position: Position = mock(classOf[Position])
    val outcome: ShotOutcome.Value = ShotOutcome.Hit
    val fleet: Fleet = mock(classOf[Fleet])
    val updatedFleet: Fleet = mock(classOf[Fleet])

    when(fleet.shootAt(position)).thenReturn((updatedFleet, outcome))

    val board: Board = new Board(10, fleet)
    assertThat(board.shoot(position), is(outcome))
    assertThat(board.shotPositions, is(Set(position)))
  }

  test("shootable excludes already shot squares") {
    val all: Set[Position] = Positions.createGrid(10)

    val board: Board = new Board(10, someFleet)
    assertThat(board.shootable, is(all))

    board.shoot(pos(1, 1))
    assertThat(board.shootable, is(all - pos(1, 1)))

    board.shoot(pos(2, 1))
    assertThat(board.shootable, is(all - pos(1, 1) - pos(2, 1)))
  }

  test("areAllShipsSunk delegates to Fleet") {
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