package org.casa.battleships.strategy.shooting

import org.casa.battleships.Positions.neighbours
import org.casa.battleships.strategy.positionchoice.PositionChooser
import annotation.tailrec
import org.casa.battleships.{ShotOutcome, Position}
import org.casa.battleships.ShotOutcome._

class LinesShooter(chooser: PositionChooser)(delegate: Shooter) extends Shooter{
  def interestingHits(allHistory: List[(Position, ShotOutcome.Value)]): List[Position] = {
    allHistory.takeWhile(_._2!=Sunk).filter(_._2==Hit).map(_._1)
  }

  def haveElementsInCommon[T](s1: Set[T], s2: Set[T]): Boolean = {
    !((s1 & s2).isEmpty)
  }

  @tailrec
  final def endOfLine(center: Position, hits: Set[Position], direction: Position => Position): Position = {
    val next: Position = direction(center)
    if(hits.contains(next)){
      endOfLine(next, hits, direction)
    }else{
      next
    }
  }

  def findLinePassingByCenter(shootable: Set[Position], hits: Set[Position], center: Position): Option[Position] = {
    val up: Boolean = hits.contains(center.up)
    val left: Boolean = hits.contains(center.left)
    val right: Boolean = hits.contains(center.right)
    val down: Boolean = hits.contains(center.down)

    val X = true
    val o = false

    val continuations: Set[Position] = (
      if (Set((X, X, X), (o, X, X), (X, X, o)).contains((left, X, right))) {
        Set(endOfLine(center, hits, _.left), endOfLine(center, hits, _.right))
      } else {
        Set[Position]()
      }) ++
      (if (Set((X, X, X), (o, X, X), (X, X, o)).contains((up, X, down))) {
        Set(endOfLine(center, hits, _.up), endOfLine(center, hits, _.down))
      } else {
        Set[Position]()
      })
    chooser.choose(continuations & shootable)
  }

  @tailrec
  final private def findLine(shootable: Set[Position], hits: List[Position]): Option[Position] = {
    hits match {
      case head :: rest => {
        if (haveElementsInCommon(neighbours(head), rest.toSet)) {
          findLinePassingByCenter(shootable, hits.toSet, head)
        } else {
          findLine(shootable, rest)
        }
      }
      case _ => None
    }
  }

  def shoot(shootable: Set[Position], history: List[(Position, ShotOutcome.Value)]) = {
    val hits: List[Position] = interestingHits(history)
    findLine(shootable, hits).orElse(delegate.shoot(shootable, history))
  }
}