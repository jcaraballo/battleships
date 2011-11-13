package org.casa.battleships.fleet

import org.casa.battleships.fleet.Ship.positions
import org.casa.battleships.{Positions, Position}

case class Ship(squares: Set[Position], squaresHit: Set[Position]) {
  def this(initial: Position, end: Position) = {
    this (positions(initial, end), Set[Position]())
  }

  def isHorizontal: Boolean = Positions.areHorizontal(squares)

  def contains(position: Position) = squares.contains(position)

  def shootAt(position: Position): Ship = {
    if (squares.contains(position)) {
      Ship(squares, squaresHit + position)
    } else {
      throw new IllegalArgumentException("Ship does not occupy position " + position)
    }
  }

  def isSunk: Boolean = (squares == squaresHit)
}

object Ship {
  private def verticalPositions(initial: Position, end: Position): Set[Position] = {
    if (initial.row < end.row) {
      (for (row <- initial.row to end.row) yield Position(initial.column, row)).toSet
    } else {
      verticalPositions(end, initial)
    }
  }

  private def horizontalPositions(initial: Position, end: Position): Set[Position] = {
    if (initial.column < end.column) {
      (for (column <- initial.column to end.column) yield Position(column, initial.row)).toSet
    } else {
      horizontalPositions(end, initial)
    }
  }

  def positions(initial: Position, end: Position): Set[Position] = {
    if (initial.column == end.column) {
      if (initial.row == end.row) {
        throw new IllegalArgumentException("Single square ships are not allowed")
      } else {
        verticalPositions(initial, end)
      }
    } else {
      horizontalPositions(initial, end)
    }
  }
}