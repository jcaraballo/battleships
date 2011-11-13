package org.casa.battleships.fleet

import org.casa.battleships.Position

case class PatrolBoat(initial: Position, end: Position) extends Ship(initial, end)