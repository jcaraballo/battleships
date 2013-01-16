package org.casa.battleships.ascii

import org.casa.battleships.Position.pos
import org.casa.battleships.fleet._

import org.casa.battleships.ascii.ShipPrinters.createForUser
import org.casa.battleships.Position
import org.scalatest.FunSuite
import org.casa.battleships.fleet.Ship.immaculateShip

class ShipPrinterTest extends FunSuite {
  test("horizontal"){
    val ship = immaculateShip(pos(1, 1), pos(5, 1))
    expectResult('<'){createForUser.printShipSquareAt(ship, pos(1, 1))}
    expectResult('-'){createForUser.printShipSquareAt(ship, pos(2, 1))}
    expectResult('-'){createForUser.printShipSquareAt(ship, pos(3, 1))}
    expectResult('-'){createForUser.printShipSquareAt(ship, pos(4, 1))}
    expectResult('>'){createForUser.printShipSquareAt(ship, pos(5, 1))}
  }

  test("vertical"){
    val ship = immaculateShip(pos(1, 1), pos(1, 5))
    expectResult('^'){createForUser.printShipSquareAt(ship, pos(1, 1))}
    expectResult('|'){createForUser.printShipSquareAt(ship, pos(1, 2))}
    expectResult('|'){createForUser.printShipSquareAt(ship, pos(1, 3))}
    expectResult('|'){createForUser.printShipSquareAt(ship, pos(1, 4))}
    expectResult('v'){createForUser.printShipSquareAt(ship, pos(1, 5))}
  }

  test("horizontal hit"){
    val squares: Set[Position] = Set(pos(1, 1), pos(2, 1))
    val smashedShip = new Ship(new ShipLocation(squares), squares)
    expectResult('*'){createForUser.printShipSquareAt(smashedShip, pos(1, 1))}
    expectResult('*'){createForUser.printShipSquareAt(smashedShip, pos(2, 1))}
  }

  test("vertical hit"){
    val squares: Set[Position] = Set(pos(1, 1), pos(1, 2))
    val smashedShip = new Ship(new ShipLocation(squares), squares)
    expectResult('*'){createForUser.printShipSquareAt(smashedShip, pos(1, 1))}
    expectResult('*'){createForUser.printShipSquareAt(smashedShip, pos(1, 2))}
  }
}