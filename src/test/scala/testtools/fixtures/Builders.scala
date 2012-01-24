package testtools.fixtures

import org.casa.battleships.{ShotOutcome, Position}
import util.matching.Regex.MatchIterator
import org.casa.battleships.Position._
import org.casa.battleships.ShotOutcome.{Water, Hit}

object Builders {
  def createHistoryOfWater(history: String): List[(Position, ShotOutcome.Value)] = {
    val lines: MatchIterator = """\{[^}]*\}""".r.findAllIn(history)
    val stringRepresentationOfPositionsWithZeroBasedColumn: Iterator[IndexedSeq[(Char, Int)]] =
      lines.map(_.zipWithIndex.filter(((c: Char, i: Int) => i % 2 != 0).tupled).map(((c: Char, i: Int) => c).tupled).zipWithIndex)
    val charactersWithPositions: Iterator[(Char, Position)] = stringRepresentationOfPositionsWithZeroBasedColumn.zipWithIndex.flatMap(
      ((v: IndexedSeq[(Char, Int)], i: Int) => v.map((t: (Char, Int)) => (t._1, pos(t._2 + 1, i + 1)))).tupled
    )
    val waterPositions: Iterator[Position] = charactersWithPositions.filter((t: (Char, Position)) => t._1 == '·').map((t: (Char, Position)) => t._2)
    waterPositions.map((_, Water)).toList
  }
}