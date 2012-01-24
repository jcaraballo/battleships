package org.casa.battleships.fleet

import org.casa.battleships.Position

case class Ship(location: ShipLocation, squaresHit: Set[Position]) {
  def shootAt(position: Position): Ship = {
    if (location.contains(position)) {
      Ship(location, squaresHit + position)
    } else {
      throw new IllegalArgumentException("Ship does not occupy position " + position)
    }
  }

  def isSunk: Boolean = (location.squares == squaresHit)
}

object Ship {
  def immaculateShip(squares: Set[Position]) = new Ship(new ShipLocation(squares), Set[Position]())

  def immaculateShip(initial: Position, end: Position) = new Ship(new ShipLocation(initial, end), Set[Position]())
}