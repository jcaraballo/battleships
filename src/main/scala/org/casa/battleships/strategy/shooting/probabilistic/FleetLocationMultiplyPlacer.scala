package org.casa.battleships.strategy.shooting.probabilistic

import org.casa.battleships.Position
import collection.immutable.Set
import org.casa.battleships.fleet.{ShipLocation, FleetLocation}
import ShipLocationMultiplyPlacer.findAllShipLocations

object FleetLocationMultiplyPlacer {
  def findAllValidLocations(shipSizes: List[Int], available: Set[Position]): Set[FleetLocation] = {
    recursiveFindAllValidLocations(shipSizes, Set(FleetConfiguration(available)))
  }

  private def recursiveFindAllValidLocations(shipSizes: List[Int], configurations: Set[FleetConfiguration]): Set[FleetLocation] = {
    shipSizes match {
      case nextShipSize :: restShipSizes => {
        val newConfigurations: Set[FleetConfiguration] = configurations.flatMap((fleetConfiguration: FleetConfiguration) => {
          val allPossibleLocationsForNextShip: Set[ShipLocation] = findAllShipLocations(nextShipSize, fleetConfiguration.available)
          allPossibleLocationsForNextShip.map {
            (possibleLocation: ShipLocation) => new FleetConfiguration(
              fleetConfiguration.fleet + possibleLocation,
              fleetConfiguration.available -- possibleLocation.squares
            )
          }
        })

        recursiveFindAllValidLocations(restShipSizes, newConfigurations)
      }
      case empty => configurations.map(_.fleet)
    }
  }

  def findAllValidLocations_slower(shipSizes: List[Int], available: Set[Position]): Set[FleetLocation] = {
    recursiveFindAllValidLocations_slower(shipSizes, Set(FleetConfiguration(available)))
  }

  private def recursiveFindAllValidLocations_slower(shipSizes: List[Int], fleetAndAvailablePositionsPairs: Set[FleetConfiguration]): Set[FleetLocation] = {
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