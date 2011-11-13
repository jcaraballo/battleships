package org.casa.battleships.fleet

import org.junit.Test
import org.casa.battleships.Position.pos
import org.scalatest.junit.JUnitSuite
import org.casa.battleships.{Board, Position}

class FleetTest extends JUnitSuite {
  @Test def hasShips() {
    val aircraftCarrier: AircraftCarrier = AircraftCarrier(pos(1, 1), pos(5, 1))
    val battleship: Battleship = Battleship(pos(1, 2), pos(4, 2))
    val destroyer: Destroyer = Destroyer(pos(1, 3), pos(3, 3))
    val submarine: Submarine = Submarine(pos(1, 4), pos(3, 4))
    val patrolBoat: PatrolBoat = PatrolBoat(pos(1, 5), pos(2, 5))

    expect(Set[Ship](aircraftCarrier, battleship, destroyer, submarine, patrolBoat)) {
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
      fleetIncluding(AircraftCarrier(pos(1, 1), pos(5, 1)), Battleship(pos(4, 1), pos(7, 1)))
      fail()
    }
    catch {
      case e: IllegalArgumentException => Nil // expected
    }
  }

  @Test def shipAtReturnsSomeShipWhenThereIsOneAtTheGivenPosition(){
    val aircraftCarrier: AircraftCarrier = AircraftCarrier(pos(1, 1), pos(5, 1))
    expect(Some(aircraftCarrier)){
      fleetIncluding(aircraftCarrier).shipAt(pos(1,1))
    }
  }

  @Test def shipAtReturnsNoneWhenThereIsNoShipAtTheGivenPosition(){
    expect(None){
      fleetWithoutShipAtTenTen.shipAt(pos(10,10))
    }
  }

  @Test def shootAtReturnsItselfPlusWaterIfThereIsNoShipInSuchPosition(){
    fleetWithoutShipAtTenTen.shootAt(pos(10, 10))
  }

  @Test def shootAtReturnsUpdatedSelfPlusHitIfThereIsShipInSuchPositionThatDoesNotSink(){
    val newShip = new Ship(Set(pos(1, 1), pos(2, 1), pos(3, 1)), Set[Position]())
    val fleet: Fleet = new Fleet(Set(newShip))

    expect((new Fleet(Set(newShip.shootAt(pos(1, 1)))), "hit")) {
      fleet.shootAt(pos(1, 1))
    }
  }

  @Test def shootAtReturnsUpdatedSelfPlusSunkIfThereIsShipInSuchPositionThatSinks(){
    val dodgyShip = new Ship(Set(pos(1, 1), pos(2, 1), pos(3, 1)), Set(pos(1, 1), pos(2, 1)))
    val fleet: Fleet = new Fleet(Set(dodgyShip))

    val sunkShip: Ship = dodgyShip.shootAt(pos(3, 1))

    expect((new Fleet(Set(sunkShip)), "sunk")) {
      fleet.shootAt(pos(3, 1))
    }
  }

  @Test def isSunkIffAllShipsAreSunk() {
    def sunk(ps: Position*): Ship = {
      new Ship(ps.toSet, ps.toSet)
    }
    def perfect(ps: Position*): Ship = {
      new Ship(ps.toSet, Set[Position]())
    }

    expect(true) {
      new Fleet(sunk(pos(1, 1), pos(2, 1)), sunk(pos(1, 2), pos(2, 2), pos(3, 2))).isSunk
    }
    expect(false) {
      new Fleet(sunk(pos(1, 1), pos(2, 1)), perfect(pos(1, 2), pos(2, 2), pos(3, 2))).isSunk
    }
    expect(false) {
      new Fleet(perfect(pos(1, 1), pos(2, 1)), perfect(pos(1, 2), pos(2, 2), pos(3, 2))).isSunk
    }
  }

  private def fleetIncluding(aircraftCarrier: AircraftCarrier, battleship: Battleship): Fleet = {
    val fleet: Fleet = new Fleet(
      aircraftCarrier,
      battleship,
      Destroyer(pos(1, 3), pos(3, 3)),
      Submarine(pos(1, 4), pos(3, 4)),
      PatrolBoat(pos(1, 5), pos(2, 5))
    )
    fleet
  }

  private def fleetIncluding(aircraftCarrier: AircraftCarrier): Fleet = {
    fleetIncluding(aircraftCarrier, Battleship(pos(1, 2), pos(4, 2)))
  }

  private def fleetWithoutShipAtTenTen: Fleet = {
    val aircraftCarrier: AircraftCarrier = AircraftCarrier(pos(1, 1), pos(5, 1))
    val battleship: Battleship = Battleship(pos(1, 2), pos(4, 2))
    val destroyer: Destroyer = Destroyer(pos(1, 3), pos(3, 3))
    val submarine: Submarine = Submarine(pos(1, 4), pos(3, 4))
    val patrolBoat: PatrolBoat = PatrolBoat(pos(1, 5), pos(2, 5))
    new Fleet(
      aircraftCarrier,
      battleship,
      destroyer,
      submarine,
      patrolBoat
    )
  }
}