package org.casa.battleships.strategy.shooting.probabilistic

import org.casa.battleships.Position
import collection.immutable.Set
import org.casa.battleships.fleet.{ShipLocation, FleetLocation}
import ShipLocationMultiplyPlacer.findAllShipLocations

object FleetLocationMultiplyPlacer {
  def findAllValidLocations(shipSizes: List[Int], available: Set[Position]): Set[FleetLocation] = {
    recursiveFindAllValidLocations(shipSizes, Set(FleetConfiguration(available)))
  }

  private def recursiveFindAllValidLocations(shipSizes: List[Int], fleetAndAvailablePositionsPairs: Set[FleetConfiguration]): Set[FleetLocation] = {
    shipSizes match {
      case nextShipSize :: restShipSizes => {
        fleetAndAvailablePositionsPairs.flatMap((fleetConfiguration: FleetConfiguration) => {
          val newFleetConfigurations: Set[FleetConfiguration] = findAllShipLocations(nextShipSize, fleetConfiguration.available).map {
            (possibleLocation: ShipLocation) => new FleetConfiguration(fleetConfiguration.fleet + possibleLocation, fleetConfiguration.available -- possibleLocation.squares)
          }
          recursiveFindAllValidLocations(restShipSizes, newFleetConfigurations)
        })
      }
      case empty => fleetAndAvailablePositionsPairs.map(_.fleet)
    }
  }
}