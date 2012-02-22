package org.casa.battleships.strategy.shooting.probabilistic

import org.casa.battleships.Position
import collection.immutable.Set
import org.casa.battleships.fleet.{ShipLocation, FleetLocation}
import ShipLocationMultiplyPlacer.findAllShipLocations

object FleetLocationMultiplyPlacer {
  def findAllValidLocations(shipSizes: List[Int], available: Set[Position]): Set[FleetLocation] = {
    recursiveFindAllValidLocations(shipSizes, Set((new FleetLocation(Set()), available)))
  }

  private def recursiveFindAllValidLocations(shipSizes: List[Int], fleetAndAvailablePositionsPairs: Set[(FleetLocation, Set[Position])]): Set[FleetLocation] = {
    shipSizes match {
      case nextShipSize :: restShipSizes => {
        fleetAndAvailablePositionsPairs.flatMap(((fleet: FleetLocation, available: Set[Position]) => {
          val newFleetAndAvailablePositionsPairs: Set[(FleetLocation, Set[Position])] = findAllShipLocations(nextShipSize, available).map {
            (possibleLocation: ShipLocation) => (fleet + possibleLocation, available -- possibleLocation.squares)
          }
          recursiveFindAllValidLocations(restShipSizes, newFleetAndAvailablePositionsPairs)
        }).tupled)
      }
      case empty => fleetAndAvailablePositionsPairs.map(_._1)
    }
  }
}