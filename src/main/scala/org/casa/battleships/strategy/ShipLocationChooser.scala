package org.casa.battleships.strategy

import org.casa.battleships.strategy.positionchoice.PositionChooser
import org.casa.battleships.Position
import annotation.tailrec
import org.casa.battleships.Positions._
import org.casa.battleships.fleet.ShipLocation

class ShipLocationChooser(chooser: PositionChooser) {
  @tailrec
  final def place(shipSize: Int, available: Set[Position]): Option[ShipLocation] = {
    chooser.choose(available) match {
      case None => None
      case Some(candidate) => {
        val possibleContinuations: Set[Position] = neighbours(candidate) & available
        placeShip(shipSize, available, Set(candidate), possibleContinuations) match {
          case None => place(shipSize, available - candidate)
          case e => e
        }
      }
    }
  }

  def placeShip(shipSize: Int, available: Set[Position], chosen: Set[Position], possibleContinuations: Set[Position]): Option[ShipLocation] = {
    if (chosen.size == shipSize) {
      Some(new ShipLocation(chosen))
    } else {
      chooser.choose(possibleContinuations) match {
        case None => None
        case Some(chosenContinuation) => {
          val newChosen: Set[Position] = chosen + chosenContinuation
          val newAvailable: Set[Position] = available - chosenContinuation
          val placement: Option[ShipLocation] = placeShip(
            shipSize,
            newAvailable,
            newChosen,
            ShipLocationChooser.calculatePossibleContinuations(newChosen) & newAvailable
          )
          placement match {
            case None => placeShip(shipSize, available, chosen, possibleContinuations - chosenContinuation)
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