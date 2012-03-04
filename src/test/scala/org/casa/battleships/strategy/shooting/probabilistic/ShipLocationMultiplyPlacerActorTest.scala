package org.casa.battleships.strategy.shooting.probabilistic

import org.junit.Assert._
import org.casa.battleships.Position
import testtools.Matchers._
import akka.util.duration._
import akka.dispatch.Await
import akka.pattern.ask
import org.scalatest.{BeforeAndAfterEach, FunSuite}
import akka.util.{Duration, Timeout}
import akka.actor.{ActorRef, Props, ActorSystem}
import org.casa.battleships.Position._
import org.hamcrest.CoreMatchers._
import org.casa.battleships.fleet.ShipLocation
import org.casa.battleships.Positions._
import collection.immutable.Set

class ShipLocationMultiplyPlacerActorTest extends FunSuite with BeforeAndAfterEach {

  test("No possible placement when no free space") {
    assertThat(findThemAll(2, Set[Position]()), isEmpty)
  }

  test("One possible placement when one place where it fits") {
    assertThat(findThemAll(2, Set(pos(1, 1), pos(2, 1))), is(Set(new ShipLocation(pos(1, 1), pos(2, 1)))))
  }

  test("All possible locations for 2 square ship in 2x2 grid") {
    assertThat(findThemAll(2, createGrid(2)), is(Set(

      //horizontal locations
      new ShipLocation(pos(1, 1), pos(2, 1)),
      new ShipLocation(pos(1, 2), pos(2, 2)),

      //verticals locations
      new ShipLocation(pos(1, 1), pos(1, 2)),
      new ShipLocation(pos(2, 1), pos(2, 2))

    )))
  }

  test("All possible locations for 2 square ship in 3x3 grid") {
    assertThat(findThemAll(2, createGrid(3)), is(Set(

      //horizontal locations
      new ShipLocation(pos(1, 1), pos(2, 1)),
      new ShipLocation(pos(2, 1), pos(3, 1)),
      new ShipLocation(pos(1, 2), pos(2, 2)),
      new ShipLocation(pos(2, 2), pos(3, 2)),
      new ShipLocation(pos(1, 3), pos(2, 3)),
      new ShipLocation(pos(2, 3), pos(3, 3)),

      //verticals locations
      new ShipLocation(pos(1, 1), pos(1, 2)),
      new ShipLocation(pos(1, 2), pos(1, 3)),
      new ShipLocation(pos(2, 1), pos(2, 2)),
      new ShipLocation(pos(2, 2), pos(2, 3)),
      new ShipLocation(pos(3, 1), pos(3, 2)),
      new ShipLocation(pos(3, 2), pos(3, 3))
    )))
  }

  val duration: Duration = 1 second
  implicit val timeout = Timeout(duration)
  var actorSystem: ActorSystem = _
  var shipPlacer: ActorRef = _

  private def findThemAll(shipSize: Int, availability: Set[Position]): Set[ShipLocation] = {
    val future = shipPlacer.ask(ShipLocationMultiplyPlacerActor.Request(FleetConfiguration(availability), shipSize))
    val response = Await.result(future, duration).asInstanceOf[ShipLocationMultiplyPlacerActor.Response]

    response.allShipLocations
  }

  override def beforeEach() {
    actorSystem = ActorSystem("MySystem")
    shipPlacer = actorSystem.actorOf(Props[ShipLocationMultiplyPlacerActor], name = "ship_placer")

  }

  override def afterEach() {
    actorSystem.shutdown
  }
}