package org.casa.battleships.fleet

import org.junit.Test
import org.casa.battleships.Position
import org.casa.battleships.Position.pos
import org.scalatest.junit.JUnitSuite

class ShipTest extends JUnitSuite {
  @Test def verticalDownwardsPatrolBoat() {
    expect(Set(pos(1, 1), pos(1, 2))) {
      new Ship(pos(1, 1), pos(1, 2)).squares
    }
  }

  @Test def verticalUpwardsPatrolBoat() {
    expect(Set(pos(1, 1), pos(1, 2))) {
      new Ship(pos(1, 1), pos(1, 2)).squares
    }
  }

  @Test def horizontalLeftToRightPatrolBoat() {
    expect(Set(pos(1, 1), pos(2, 1))) {
      new Ship(pos(1, 1), pos(2, 1)).squares
    }
  }

  @Test def horizontalRightToLeftPatrolBoat() {
    expect(Set(pos(2, 1), pos(1, 1))) {
      new Ship(pos(2, 1), pos(1, 1)).squares
    }
  }

  @Test def singleSquareShipsAreNotAllowed() {
    try {
      new Ship(pos(1, 1), pos(1, 1))
      fail()
    } catch {
      case e: IllegalArgumentException => Nil //expected
    }
  }

  @Test def isHorizontalIfAndOnlyIfAllPositionsAreInTheSameRow() {
    expect(true) {new Ship(pos(1, 1), pos(5, 1)).isHorizontal}
    expect(false) {new Ship(pos(1, 1), pos(1, 5)).isHorizontal}
  }

  @Test def shootAtReturnsShipWithSuchPositionHit(){
    expect(new Ship(Set(pos(1, 1), pos(2, 1)), Set(pos(1,1)))){
      new Ship(pos(1, 1), pos(2, 1)).shootAt(pos(1,1))
    }
  }

  @Test def isSunkIfAndOnlyIfAllSquaresHaveBeenHit(){
    val ship: Ship = new Ship(pos(1, 1), pos(2, 1))
    expect(false){
      ship.isSunk
    }

    expect(false){
      ship.shootAt(pos(1,1)).isSunk
    }
    
    expect(true){
      ship.shootAt(pos(1,1)).shootAt(pos(2,1)).isSunk
    }
  }

  @Test def shootAtBlowsUpWhenShipDoesNotOccupySuchSquare(){
    try{
      new Ship(pos(1, 1), pos(2, 1)).shootAt(pos(2,2))
      fail()
    }catch{
      case e: IllegalArgumentException => Nil //expected
    }
  }
}