package org.casa.battleships.strategy

import org.casa.battleships.strategy.positionchoice.PositionChooser
import org.casa.battleships.Position
import annotation.tailrec
import org.casa.battleships.Positions._
import org.casa.battleships.fleet.ShipLocation

class ShipLocationChooser(chooser: PositionChooser) {
  @tailrec
  final def place(shipSize: Int, availability: Set[Position]): Option[ShipLocation] = {
    chooser.choose(availability) match {
      case None => None
      case Some(candidate) => {
        val possibleContinuations: Set[Position] = neighbours(candidate) & availability
        placeShip(shipSize, availability, Set(candidate), possibleContinuations) match {
          case None => place(shipSize, availability - candidate)
          case e => e
        }
      }
    }
  }

  def placeShip(shipSize: Int, availability: Set[Position], chosen: Set[Position], possibleContinuations: Set[Position]): Option[ShipLocation] = {
    if (chosen.size == shipSize) {
      Some(new ShipLocation(chosen))
    } else {
      chooser.choose(possibleContinuations) match {
        case None => None
        case Some(chosenContinuation) => {
          val newChosen: Set[Position] = chosen + chosenContinuation
          val newAvailability: Set[Position] = availability - chosenContinuation
          val placement: Option[ShipLocation] = placeShip(
            shipSize,
            newAvailability,
            newChosen,
            ShipLocationChooser.calculatePossibleContinuations(newChosen) & newAvailability
          )
          placement match {
            case None => placeShip(shipSize, availability, chosen, possibleContinuations - chosenContinuation)
            case e => e
          }
        }
      }
    }
  }
}

object ShipLocationChooser {
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