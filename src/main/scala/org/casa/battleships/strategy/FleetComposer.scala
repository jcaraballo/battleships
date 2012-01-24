package org.casa.battleships.strategy

import org.casa.battleships.Positions._
import annotation.tailrec
import org.casa.battleships.strategy.positionchoice.PositionChooser
import org.casa.battleships.Position
import org.casa.battleships.fleet.{Ship, Fleet}

class FleetComposer(chooser: PositionChooser){
  def create(gridSize: Int, shipSizes: List[Int]): Option[Fleet] = {
    placeShips(shipSizes, createGrid(gridSize), new Fleet())
  }

  @tailrec
  private def placeShips(shipSizes: List[Int], availablePositions: Set[Position], fleet: Fleet): Option[Fleet] = shipSizes match {
    case head :: rest => new ShipLocationChooser(chooser).place(head, availablePositions) match {
      case Some(shipLocation) => {
        val newFleet: Fleet = fleet + new Ship(shipLocation, Set())
        placeShips(rest, availablePositions -- shipLocation.squares, newFleet)
      }
      case None => None
    }
    case _ => Some(fleet)
  }
}