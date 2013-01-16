package org.casa.battleships.fleet

import org.junit.Test
import org.casa.battleships.Position.pos
import org.scalatest.junit.JUnitSuite
import org.casa.battleships.Position
import org.casa.battleships.ShotOutcome._
import Ship.immaculateShip

class FleetTest extends JUnitSuite {
  @Test def hasShips() {
    val aircraftCarrier = immaculateShip(pos(1, 1), pos(5, 1))
    val battleship = immaculateShip(pos(1, 2), pos(4, 2))
    val destroyer = immaculateShip(pos(1, 3), pos(3, 3))
    val submarine = immaculateShip(pos(1, 4), pos(3, 4))
    val patrolBoat = immaculateShip(pos(1, 5), pos(2, 5))

    expectResult(Set[Ship](aircraftCarrier, battleship, destroyer, submarine, patrolBoat)) {
      new Fleet(
        aircraftCarrier,
        battleship,
        destroyer,
        submarine,
        patrolBoat
      ).ships
    }
  }

  @Test def shipsSharingSquareCannotFormAValidFleet() {
    try {
      fleetIncluding(immaculateShip(pos(1, 1), pos(5, 1)), immaculateShip(pos(4, 1), pos(7, 1)))
      fail()
    }
    catch {
      case e: IllegalArgumentException => Nil // expected
    }
  }

  @Test def shipAtReturnsSomeShipWhenThereIsOneAtTheGivenPosition(){
    val aircraftCarrier = immaculateShip(pos(1, 1), pos(5, 1))
    expectResult(Some(aircraftCarrier)){
      fleetIncluding(aircraftCarrier).shipAt(pos(1,1))
    }
  }

  @Test def shipAtReturnsNoneWhenThereIsNoShipAtTheGivenPosition(){
    expectResult(None){
      fleetWithoutShipAtTenTen.shipAt(pos(10,10))
    }
  }

  @Test def shootAtReturnsItselfPlusWaterIfThereIsNoShipInSuchPosition(){
    expectResult((fleetWithoutShipAtTenTen, Water)){
      fleetWithoutShipAtTenTen.shootAt(pos(10, 10))
    }
  }

  @Test def shootAtReturnsUpdatedSelfPlusHitIfThereIsShipInSuchPositionThatDoesNotSink(){
    val newShip = immaculateShip(Set(pos(1, 1), pos(2, 1), pos(3, 1)))
    val fleet: Fleet = new Fleet(Set(newShip))

    expectResult((new Fleet(Set(newShip.shootAt(pos(1, 1)))), Hit)) {
      fleet.shootAt(pos(1, 1))
    }
  }

  @Test def shootAtReturnsUpdatedSelfPlusSunkIfThereIsShipInSuchPositionThatSinks(){
    val dodgyShip = new Ship(new ShipLocation(Set(pos(1, 1), pos(2, 1), pos(3, 1))), Set(pos(1, 1), pos(2, 1)))
    val fleet: Fleet = new Fleet(Set(dodgyShip))

    val sunkShip: Ship = dodgyShip.shootAt(pos(3, 1))

    expectResult((new Fleet(Set(sunkShip)), Sunk)) {
      fleet.shootAt(pos(3, 1))
    }
  }

  @Test def isSunkIffAllShipsAreSunk() {
    def sunk(ps: Position*): Ship = {
      new Ship(new ShipLocation(ps.toSet), ps.toSet)
    }
    def perfect(ps: Position*): Ship = {
      new Ship(new ShipLocation(ps.toSet), Set[Position]())
    }

    expectResult(true) {
      new Fleet(sunk(pos(1, 1), pos(2, 1)), sunk(pos(1, 2), pos(2, 2), pos(3, 2))).isSunk
    }
    expectResult(false) {
      new Fleet(sunk(pos(1, 1), pos(2, 1)), perfect(pos(1, 2), pos(2, 2), pos(3, 2))).isSunk
    }
    expectResult(false) {
      new Fleet(perfect(pos(1, 1), pos(2, 1)), perfect(pos(1, 2), pos(2, 2), pos(3, 2))).isSunk
    }
  }

  private def fleetIncluding(ships: Ship*): Fleet = {
    new Fleet(ships.toSet + immaculateShip(pos(1, 3), pos(3, 3)) + immaculateShip(pos(1, 4), pos(3, 4)) + immaculateShip(pos(1, 5), pos(2, 5)))
  }

  private def fleetWithoutShipAtTenTen: Fleet = {
    new Fleet(
      immaculateShip(pos(1, 1), pos(5, 1)),
      immaculateShip(pos(1, 2), pos(4, 2)),
      immaculateShip(pos(1, 3), pos(3, 3)),
      immaculateShip(pos(1, 4), pos(3, 4)),
      immaculateShip(pos(1, 5), pos(2, 5))
    )
  }
}