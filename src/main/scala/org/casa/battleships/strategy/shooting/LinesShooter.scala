package org.casa.battleships.strategy.shooting

import org.casa.battleships.Position
import org.casa.battleships.Positions.neighbours
import org.casa.battleships.strategy.positionchoice.PositionChooser
import annotation.tailrec

class LinesShooter(chooser: PositionChooser)(delegate: Shooter) extends Shooter{
  def interestingHits(allHistory: List[(Position, String)]): List[Position] = {
    allHistory.takeWhile(_._2!="sunk").filter(_._2=="hit").map(_._1)
  }

  def haveElementsInCommon[T](s1: Set[T], s2: Set[T]): Boolean = {
    !((s1 & s2).isEmpty)
  }

  def findLinePassingByCenter(shootable: Set[Position], hits: List[Position], center: Position): Option[Position] = {
    def tryWith(ps: List[Position]): Option[Position] = chooser.choose(ps.toSet & shootable)

    val up: Boolean = hits.contains(center.up)
    val left: Boolean = hits.contains(center.left)
    val right: Boolean = hits.contains(center.right)
    val down: Boolean = hits.contains(center.down)
    val neighbourhood = (up, left, true, right, down)

    val X = true
    val o = false

    neighbourhood match {
      case (_,
         X, X, X,
            _)    => tryWith(center.left.left :: center.right.right :: Nil)

      case (X,
         _, X, _,
            X)    => tryWith(center.up.up :: center.down.down :: Nil)

      case (_,
         X, X, o,
            _)    => tryWith(center.left.left :: center.right :: Nil)

      case (_,
         o, X, X,
            _)    => tryWith(center.left :: center.right.right :: Nil)

      case (X,
         _, X, _,
            o)    => tryWith(center.up.up :: center.down :: Nil)

      case (o,
         _, X, _,
            X)    => tryWith(center.up :: center.down.down :: Nil)
      case _ => None
    }
  }

  @tailrec
  final def findLine(shootable: Set[Position], hits: List[Position]): Option[Position] = {

    hits match {
      case head :: rest => {
        val otherHits: Set[Position] = rest.toSet
        if (haveElementsInCommon(neighbours(head), otherHits)) {
          findLinePassingByCenter(shootable, hits, head)
        } else {
          findLine(shootable, rest)
        }
      }
      case _ => None
    }
  }

  def shoot(shootable: Set[Position], history: List[(Position, String)]) = {
    val hits: List[Position] = interestingHits(history)
    val found: Option[Position] = findLine(shootable, hits)
    found.orElse(delegate.shoot(shootable, history))
  }
}