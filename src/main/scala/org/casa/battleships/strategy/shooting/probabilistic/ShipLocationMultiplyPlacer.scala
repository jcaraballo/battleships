package org.casa.battleships.strategy.shooting.probabilistic

import org.casa.battleships.Position
import org.casa.battleships.Positions._
import org.casa.battleships.fleet.ShipLocation
import collection.immutable.Set

object ShipLocationMultiplyPlacer {
  def findAllShipLocations(shipSize: Int, availability: Set[Position]): Set[ShipLocation] = {
    availability.map(candidate => {
      val possibleContinuations: Set[Position] = neighbours(candidate) & availability
      placeShip(shipSize, availability, Set(candidate), possibleContinuations)
    }).flatten
  }

  def placeShip(shipSize: Int, availability: Set[Position], chosen: Set[Position], possibleContinuations: Set[Position]): Set[ShipLocation] = {
    if (chosen.size == shipSize) {
      Set(new ShipLocation(chosen))
    } else {
      possibleContinuations.map(continuation => {
                placeShip(
                  shipSize,
                  availability - continuation,
                  chosen + continuation,
                  ShipLocationMultiplyPlacer.calculatePossibleContinuations(chosen + continuation) & availability - continuation
                )
              }
      ).flatten
    }
  }

  def calculatePossibleContinuations(chosen: Set[Position]): Set[Position] = {
    if (chosen.size == 1) {
      neighbours(chosen.head)
    } else if (areHorizontal(chosen)) {
      Set(leftmost(chosen).left, rightmost(chosen).right)
    } else {
      Set(upmost(chosen).up, downmost(chosen).down)
    }
  }
}