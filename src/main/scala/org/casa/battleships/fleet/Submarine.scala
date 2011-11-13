package org.casa.battleships.fleet

import org.casa.battleships.Position

case class Submarine(initial: Position, end: Position) extends Ship(initial, end)