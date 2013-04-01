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
import org.scalatest.matchers.ShouldMatchers

class GameFunctionalTest extends FunSuite with BeforeAndAfterAll with ShouldMatchers {
  val shipSizes: Bag[Int] = Bag(5)
  var actorSystem: ActorSystem = _

  test("Finds immaculate ship when there is only one possible place for it") {
    val computerPlayer = new ComputerPlayer(new NewBestShooter(new UpmostAndThenLeftmostPositionChooser, actorSystem, shipSizes, 20 seconds), 10)

    val history = createHistoryOfWater("""
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

    computerPlayer.play(history) shouldBe pos(1, 10)
  }

  override protected def beforeAll() {
    actorSystem = ActorSystem("MySystem")
  }

  override protected def afterAll() {
    actorSystem.shutdown()
  }
}