package org.casa.battleships.ascii

import org.casa.battleships.fleet.Ship
import org.casa.battleships.Position

class ShipPrinter(
  val leftEnd: Char,
  val horizontalMiddle: Char,
  val rightEnd: Char,
  val topEnd: Char,
  val verticalMiddle: Char,
  val bottomEnd: Char,
  val hit: Char){

  def printShipSquareAt(ship: Ship, position: Position): Char = {
    if (!ship.squares.contains(position)){
      throw new IllegalArgumentException("Ship " + ship + " does not occupy position " + position)
    }

    if(ship.squaresHit.contains(position)){
      return hit
    }

    val X = true
    val o = false

    if(ship.isHorizontal){
      val neighbourhood = (ship.contains(position.left), true, ship.contains(position.right))
      neighbourhood match {
        case (X, X, X) => horizontalMiddle
        case (X, X, o) => rightEnd
        case (o, X, X) => leftEnd
        case _ => throw new IllegalArgumentException("Invalid ship: " + ship)
      }

    }else{
      val neighbourhood = (ship.contains(position.up), true, ship.contains(position.down))
      neighbourhood match {
        case (
            X,
            X,
            X) => verticalMiddle
        case (
            X,
            X,
            o) => bottomEnd
        case (
            o,
            X,
            X) => topEnd
        case _ => throw new IllegalArgumentException("Invalid ship: " + ship)
      }
    }
  }
}