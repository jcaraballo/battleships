package org.casa.battleships.strategy

import org.casa.battleships.Position
import collection.immutable.Set
import org.casa.battleships.fleet.{ShipLocation, FleetLocation}
import org.casa.battleships.strategy.ShipLocationMultiplyPlacer.findAllShipLocations

object FleetLocationMultiplyPlacer {
  def findAllValidLocations(shipSizes: List[Int], available: Set[Position]): Set[FleetLocation] = {
    shipSizes match {
      case next :: rest => {
        val fleetsWithWhatTheyLeaveAvailable = findAllShipLocations(next, available).map {
          shipLocation =>
            (new FleetLocation(Set(shipLocation)), available -- shipLocation.squares)
        }
        recursiveFindAllValidLocations(rest, fleetsWithWhatTheyLeaveAvailable)
      }
      case empty => Set()
    }
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