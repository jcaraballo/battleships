package org.casa.battleships.strategy.shooting.probabilistic

import org.casa.battleships.Position
import collection.immutable.Set
import org.casa.battleships.fleet.{ShipLocation, FleetLocation}
import ShipLocationMultiplyPlacer.findAllShipLocations

object FleetLocationMultiplyPlacer {
  def findAllValidLocations(shipSizes: List[Int], available: Set[Position]): Set[FleetLocation] = {
    recursiveFindAllValidLocations(shipSizes, Set((new FleetLocation(Set()), available)))
  }

  private def recursiveFindAllValidLocations(shipSizes: List[Int], validFleetWithWhatLeavesAvailable: Set[(FleetLocation, Set[Position])]): Set[FleetLocation] = {
    shipSizes match {
      case nextShipSize :: restShipSizes => {
        val t =
          (fleet: FleetLocation, available: Set[Position]) => {
            val lol: Set[(FleetLocation, Set[Position])] = findAllShipLocations(nextShipSize, available).map {
              (possibleLocation: ShipLocation) => (fleet + possibleLocation, available -- possibleLocation.squares)
            }
            recursiveFindAllValidLocations(restShipSizes, lol)
          }
        validFleetWithWhatLeavesAvailable.flatMap(t.tupled)
      }
      case empty => validFleetWithWhatLeavesAvailable.map(_._1)
    }
  }
}