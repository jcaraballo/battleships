package org.casa.battleships.strategy.shooting.probabilistic

import org.casa.battleships.Position
import collection.immutable.Set
import org.casa.battleships.fleet.FleetLocation

class FleetLocationMultiplyPlacer(shipPlacer: ShipLocationMultiplyPlacer) {
  def findAllValidLocations(shipSizes: List[Int], availability: Set[Position]): Set[FleetLocation] = {
    recursiveFindAllValidLocations(shipSizes, Set(FleetConfiguration(availability)))
  }

  private def recursiveFindAllValidLocations(shipSizes: List[Int], configurations: Set[FleetConfiguration]): Set[FleetLocation] = {
    shipSizes match {
      case nextShipSize :: restShipSizes => {
        val newConfigurations: Set[FleetConfiguration] = configurations.flatMap(fleetConfiguration => {
          val allPossibleLocationsForNextShip = shipPlacer.findAllShipLocations(nextShipSize, fleetConfiguration.availability)
          allPossibleLocationsForNextShip.map { possibleLocation => fleetConfiguration + possibleLocation }
        })

        recursiveFindAllValidLocations(restShipSizes, newConfigurations)
      }
      case empty => configurations.map(_.fleet)
    }
  }
}