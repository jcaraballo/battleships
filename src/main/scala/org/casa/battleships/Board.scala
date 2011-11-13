package org.casa.battleships

import ascii.BoardPrinters
import org.casa.battleships.fleet.Fleet

final class Board (val size: Int, var fleet: Fleet, var shotPositions: Set[Position]) {
  def this(size: Int, fleet: Fleet) = this(size, fleet, Set[Position]())

  def shoot(position: Position): ShotOutcome.Value = {
    val (newFleet, outcome): (Fleet, ShotOutcome.Value) = fleet.shootAt(position)
    fleet = newFleet
    shotPositions += position
    outcome
  }

  def available: Set[Position] = Positions.createGrid(size).filter(fleet.shipAt(_) match {
    case Some(_) => false
    case None => true
  })

  def shootable: Set[Position] = Positions.createGrid(size) -- shotPositions

  def areAllShipsSunk: Boolean = fleet.isSunk

  override def toString = BoardPrinters.createForUser.toAscii(this)

  override def equals(other: Any): Boolean = other match {
    case that: Board => this.size == that.size && this.fleet == that.fleet && this.shotPositions == that.shotPositions
    case _ => false
  }

  override def hashCode(): Int = 41 * (41 * (41 + size.hashCode()) + fleet.hashCode()) * shotPositions.hashCode()
}