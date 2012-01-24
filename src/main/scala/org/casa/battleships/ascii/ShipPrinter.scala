package org.casa.battleships.ascii

import org.casa.battleships.Position
import org.casa.battleships.fleet.{ShipLocation, Ship}

class ShipPrinter(
  val leftEnd: Char,
  val horizontalMiddle: Char,
  val rightEnd: Char,
  val topEnd: Char,
  val verticalMiddle: Char,
  val bottomEnd: Char,
  val hit: Char){

  def printShipSquareAt(ship: Ship, position: Position): Char = {
    val location: ShipLocation = ship.location
    if (!location.contains(position)){
      throw new IllegalArgumentException("Ship " + ship + " does not occupy position " + position)
    }

    if(ship.squaresHit.contains(position)){
      return hit
    }

    val X = true
    val o = false

    if(location.isHorizontal){
      val neighbourhood = (location.contains(position.left), true, location.contains(position.right))
      neighbourhood match {
        case (X, X, X) => horizontalMiddle
        case (X, X, o) => rightEnd
        case (o, X, X) => leftEnd
        case _ => throw new IllegalArgumentException("Invalid ship: " + ship)
      }

    }else{
      val neighbourhood = (location.contains(position.up), true, location.contains(position.down))
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