package functional

import org.scalatest.{BeforeAndAfterAll, FunSuite}
import org.casa.battleships.Position.pos
import org.casa.battleships.ShotOutcome.{Water, Hit}
import org.junit.Assert.assertThat
import org.hamcrest.CoreMatchers.is
import org.casa.battleships._
import fleet.Bag
import strategy.positionchoice.UpmostAndThenLeftmostPositionChooser
import strategy.shooting.{NewBestShooter, OneOneShooter}
import testtools.fixtures.Examples.somePosition
import testtools.fixtures.Examples.classicListOfShipSizes
import testtools.fixtures.Builders.createHistoryOfWater
import akka.actor.ActorSystem
import org.casa.battleships.Turn
import scala.concurrent.duration._

class GameFunctionalTest extends FunSuite with BeforeAndAfterAll{
  val shipSizes: Bag[Int] = Bag(5)
  var actorSystem: ActorSystem = _

  test("Plays game where user starts") {
    val computerPlayer = new ComputerPlayer(new UpmostAndThenLeftmostPositionChooser, new OneOneShooter, 10, classicListOfShipSizes)

    assertThat(computerPlayer.playFirstTurn(pos(10, 10)), is(Turn(Water, pos(1, 1))))

    assertThat(computerPlayer.play(Turn(Hit, pos(1, 1))), is(Turn(Hit, pos(1, 1))))
  }

  test("Finds immaculate ship when there is only one possible place for it") {
    val chooser: UpmostAndThenLeftmostPositionChooser = new UpmostAndThenLeftmostPositionChooser
    val computerPlayer = new ComputerPlayer(chooser, new NewBestShooter(chooser, actorSystem, shipSizes, 20 seconds), 10, 5 :: Nil)

    computerPlayer.historyOfMyShotsAtTheEnemy = createHistoryOfWater("""
  1 2 3 4 5 6 7 8 9 0
  ~~~~~~~~~~~~~~~~~~~
1{  ·   ·   ·   ·   ·}1
2{·   ·   ·   ·   ·  }2
3{  ·   ·   ·   ·   ·}3
4{·   ·   ·   ·   ·  }4
5{  ·   ·   ·   ·   ·}5
6{·   ·   ·   ·   ·  }6
7{  ·   ·   ·   ·   ·}7
8{·   ·   ·   ·   ·  }8
9{· · · · · · · · · ·}9
0{          · · · · ·}0
  ~~~~~~~~~~~~~~~~~~~
  1 2 3 4 5 6 7 8 9 0
""")

    assertThat(computerPlayer.playFirstTurn(somePosition).shotBack, is(pos(1, 10)))
  }

  override protected def beforeAll() {
    actorSystem = ActorSystem("MySystem")
  }

  override protected def afterAll() {
    actorSystem.shutdown()
  }
}