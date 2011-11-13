package org.casa.battleships.ascii

import org.junit.Test
import org.casa.battleships.Position.pos
import org.scalatest.junit.JUnitSuite
import org.casa.battleships.fleet._

import org.casa.battleships.ascii.ShipPrinters.createForUser
import org.casa.battleships.Position

class ShipPrinterTest extends JUnitSuite {
  @Test def horizontal(){
    val ship = new Ship(pos(1, 1), pos(5, 1))
    expect('<'){createForUser.printShipSquareAt(ship, pos(1, 1))}
    expect('-'){createForUser.printShipSquareAt(ship, pos(2, 1))}
    expect('-'){createForUser.printShipSquareAt(ship, pos(3, 1))}
    expect('-'){createForUser.printShipSquareAt(ship, pos(4, 1))}
    expect('>'){createForUser.printShipSquareAt(ship, pos(5, 1))}
  }

  @Test def vertical(){
    val ship = new Ship(pos(1, 1), pos(1, 5))
    expect('^'){createForUser.printShipSquareAt(ship, pos(1, 1))}
    expect('|'){createForUser.printShipSquareAt(ship, pos(1, 2))}
    expect('|'){createForUser.printShipSquareAt(ship, pos(1, 3))}
    expect('|'){createForUser.printShipSquareAt(ship, pos(1, 4))}
    expect('v'){createForUser.printShipSquareAt(ship, pos(1, 5))}
  }

  @Test def horizontalHit(){
    val squares: Set[Position] = Set(pos(1, 1), pos(2, 1))
    val smashedShip = new Ship(squares, squares)
    expect('*'){createForUser.printShipSquareAt(smashedShip, pos(1, 1))}
    expect('*'){createForUser.printShipSquareAt(smashedShip, pos(2, 1))}
  }

  @Test def verticalHit(){
    val squares: Set[Position] = Set(pos(1, 1), pos(1, 2))
    val smashedShip = new Ship(squares, squares)
    expect('*'){createForUser.printShipSquareAt(smashedShip, pos(1, 1))}
    expect('*'){createForUser.printShipSquareAt(smashedShip, pos(1, 2))}
  }
}