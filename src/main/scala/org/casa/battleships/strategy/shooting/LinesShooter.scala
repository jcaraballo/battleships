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

  def findPossibleEndsOfLinePassingByCenter(hits: Set[Position], center: Position): Set[Position] = {
    (
      if (hits.contains(center.left) || hits.contains(center.right)) {
        Set(endOfLine(center, hits, _.left), endOfLine(center, hits, _.right))
      } else {
        Set[Position]()
      }
    ) ++ (
      if (hits.contains(center.up) || hits.contains(center.down)) {
        Set(endOfLine(center, hits, _.up), endOfLine(center, hits, _.down))
      } else {
        Set[Position]()
      }
    )
  }

  @tailrec
  final private def findLine(shootable: Set[Position], hits: List[Position]): Option[Position] = {
    hits match {
      case head :: rest => {
        if (haveElementsInCommon(neighbours(head), rest.toSet)) {
          val continuations: Set[Position] = findPossibleEndsOfLinePassingByCenter(hits.toSet, head)
          chooser.choose(continuations & shootable)
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