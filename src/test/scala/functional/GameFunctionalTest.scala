package functional

import org.scalatest.FunSuite
import org.casa.battleships.strategy.positionchoice.UpmostAndThenLeftmostPositionChooser
import org.casa.battleships.strategy.shooting.OneOneShooter
import org.casa.battleships.Position.pos
import org.casa.battleships.ShotOutcome.{Water, Hit}
import org.junit.Assert.assertThat
import org.hamcrest.CoreMatchers.is
import org.hamcrest.CoreMatchers.anyOf
import org.casa.battleships.strategy.shooting.Shooters.bestShooter
import collection.Iterator
import collection.immutable.IndexedSeq
import util.matching.Regex.MatchIterator
import org.casa.battleships._

class GameFunctionalTest extends FunSuite {
  test("Plays game where user starts") {
    val computerPlayer = new ComputerPlayer(new UpmostAndThenLeftmostPositionChooser, new OneOneShooter, 10, 5 :: 4 :: 3 :: 3 :: 2 :: Nil)

    assertThat(computerPlayer.playFirstTurn(pos(10, 10)), is(Turn(Water, pos(1, 1))))

    assertThat(computerPlayer.play(Turn(Hit, pos(1, 1))), is(Turn(Hit, pos(1, 1))))
  }

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

  test("Finds immaculate ship when there is only one possible place for it") {
    val chooser: UpmostAndThenLeftmostPositionChooser = new UpmostAndThenLeftmostPositionChooser
    val computerPlayer = new ComputerPlayer(chooser, bestShooter(chooser), 10, 5 :: Nil)

//    val history = """
//  1 2 3 4 5 6 7 8 9 0
//  ~~~~~~~~~~~~~~~~~~~
//1{          · · · · ·}1
//2{· · · · · · · · · ·}2
//3{  ·   ·   ·   ·   ·}3
//4{·   ·   ·   ·   ·  }4
//5{  ·   ·   ·   ·   ·}5
//6{·   ·   ·   ·   ·  }6
//7{  ·   ·   ·   ·   ·}7
//8{·   ·   ·   ·   ·  }8
//9{  ·   ·   ·   ·   ·}9
//0{·   ·   ·   ·   ·  }0
//  ~~~~~~~~~~~~~~~~~~~
//  1 2 3 4 5 6 7 8 9 0
//"""
    computerPlayer.historyOfMyShotsAtTheEnemy = createHistoryOfWater("""
  1 2 3 4 5 6 7 8 9 0
  ~~~~~~~~~~~~~~~~~~~
1{          · · · · ·}1
2{· · · · · · · · · ·}2
3{· · · · · · · · · ·}3
4{· · · · · · · · · ·}4
5{· · · · · · · · · ·}5
6{· · · · · · · · · ·}6
7{· · · · · · · · · ·}7
8{· · · · · · · · · ·}8
9{· · · · · · · · · ·}9
0{· · · · · · · · · ·}0
  ~~~~~~~~~~~~~~~~~~~
  1 2 3 4 5 6 7 8 9 0
""")

    assertThat(computerPlayer.playFirstTurn(pos(1, 1)).shotBack, is(pos(1, 1)))
  }
}