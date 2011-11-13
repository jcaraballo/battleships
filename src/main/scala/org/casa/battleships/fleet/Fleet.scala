package org.casa.battleships.fleet

import scala.collection.mutable
import org.casa.battleships.Position

case class Fleet (ships: Set[Ship]) {
  def this(ships: Ship*) = this(ships.toSet)

  ensureShipsDoNotShareSquare()

  def shareSquare(ships: Set[Ship]): Boolean = {
    def repeats[T](ts: List[T]): Boolean = {
      val tsSoFar = mutable.Set[T]()
      for (t <- ts) {
        if (tsSoFar.contains(t))
          return true
        tsSoFar += t
      }
      false
    }
    repeats(ships.toList.flatMap(_.squares.toList))
  }

  def shipAt(position: Position): Option[Ship] = ships.find(_.squares.contains(position))

  def shootAt(position: Position): (Fleet, String) = {
    val affectedShip: Option[Ship] = shipAt(position)
    affectedShip match {
      case None => (this, "water")
      case Some(ship) => {
        val affectedShip: Ship = ship.shootAt(position)
        (new Fleet(ships - ship + affectedShip), if (affectedShip.isSunk) "sunk" else "hit")
      }
    }
  }

  def +(ship: Ship): Fleet = new Fleet(ships + ship)

  def isSunk: Boolean = ships.forall(_.isSunk)

  private def ensureShipsDoNotShareSquare() {
    if (shareSquare(ships)) {
      throw new IllegalArgumentException("Ships cannot share square")
    }
  }
}