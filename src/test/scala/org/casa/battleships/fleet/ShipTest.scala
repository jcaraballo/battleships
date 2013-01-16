package org.casa.battleships.fleet

import org.junit.Test
import org.casa.battleships.Position.pos
import org.scalatest.junit.JUnitSuite

class ShipTest extends JUnitSuite {
  @Test def shootAtReturnsShipWithSuchPositionHit(){
    val location: ShipLocation = new ShipLocation(Set(pos(1, 1), pos(2, 1)))
    expectResult(new Ship(location, Set(pos(1,1)))){
      new Ship(location, Set()).shootAt(pos(1,1))
    }
  }

  @Test def isSunkIfAndOnlyIfAllSquaresHaveBeenHit(){
    val ship: Ship = new Ship(new ShipLocation(pos(1, 1), pos(2, 1)), Set())
    expectResult(false){
      ship.isSunk
    }

    expectResult(false){
      ship.shootAt(pos(1,1)).isSunk
    }

    expectResult(true){
      ship.shootAt(pos(1,1)).shootAt(pos(2,1)).isSunk
    }
  }

  @Test def shootAtBlowsUpWhenShipDoesNotOccupySuchSquare(){
    val location: ShipLocation = new ShipLocation(pos(1, 1), pos(2, 1))

    try{
      new Ship(location, Set()).shootAt(pos(2,2))
      fail()
    }catch{
      case e: IllegalArgumentException => Nil //expected
    }
  }
}