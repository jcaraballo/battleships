package org.casa.battleships.strategy.shooting.probabilistic

import org.junit.Assert._
import akka.util.duration._
import akka.dispatch.Await
import akka.pattern.ask
import org.scalatest.{BeforeAndAfterEach, FunSuite}
import akka.util.{Duration, Timeout}
import akka.actor.{ActorRef, Props, ActorSystem}
import org.casa.battleships.Position._
import org.hamcrest.CoreMatchers._
import collection.immutable.Set
import testtools.fixtures.Examples._
import org.casa.battleships.fleet.{FleetLocation, ShipLocation}
import org.casa.battleships.{Positions, Position}
import testtools.fixtures.Builders._
import testtools.fixtures.Examples
import org.hamcrest.CoreMatchers
import java.util.concurrent.TimeoutException

class MasterActorTest extends FunSuite with BeforeAndAfterEach {

  test("No possible location when no available space") {
    assertThat(findThemAll(someFleetConfiguration, Set()), is(Set[FleetLocation]()))
  }

  test("Empty fleet when no ships to place") {
    assertThat(findThemAll(Nil, Positions.createGrid(10)), is(Set(new FleetLocation(Set[ShipLocation]()))))
  }

  test("All possible fleets for 2 ships of 2 squares in grid") {
    val horizontalShips = Set(
      new ShipLocation(pos(1, 1), pos(2, 1)),
      new ShipLocation(pos(1, 2), pos(2, 2)))

    val verticalShips = Set(
      new ShipLocation(pos(1, 1), pos(1, 2)),
      new ShipLocation(pos(2, 1), pos(2, 2))
    )

    assertThat(findThemAll(2 :: 2 :: Nil, Positions.createGrid(2)), is(Set(
      new FleetLocation(horizontalShips), new FleetLocation(verticalShips)
    )))
  }

  test("Finds unique fleet when there is only one place where it fits") {
    val waterPositions = createHistoryOfWater("""
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
    """).map(_._1)

    val available = Positions.createGrid(10) -- waterPositions

    assertThat(findThemAll(5 :: Nil, available), is(Set(
      new FleetLocation(Set(ShipLocation(Set(pos(1, 10), pos(2, 10), pos(3, 10), pos(4, 10), pos(5, 10)))))
    )))
  }

  test("Times out when request takes longer than time out"){
    val future = fleetPlacer.ask(MasterActor.Request(Examples.classicFleetConfiguration, Positions.createGrid(10)))
    try {
      Await.result(future, 1 milli)
      fail("Should throw a TimeoutException")
    }
    catch {
      case e: TimeoutException => null // expected
    }
  }

  val duration: Duration = 1 second
  implicit val timeout = Timeout(duration)
  var actorSystem: ActorSystem = _
  var shipsPlacer: ActorRef = _
  var fleetPlacer: ActorRef = _

  private def findThemAll(shipSizes: List[Int], availability: Set[Position]): Set[FleetLocation] = {
    val future = fleetPlacer.ask(MasterActor.Request(shipSizes, availability))
    Await.result(future, duration) match {
      case response: MasterActor.Response => response.allFleetLocations

      case exceptionalResponse: MasterActor.ExceptionalResponse => {
        throw new RuntimeException("Got ExceptionalResponse back", exceptionalResponse.exception)
      }

      case unexpected => throw new IllegalStateException("Got unexpected response back" + unexpected)
    }
  }

  override def beforeEach() {
    actorSystem = ActorSystem("MySystem")
    shipsPlacer = actorSystem.actorOf(Props(new WorkerActor(new ShipLocationMultiplyPlacer)))
    fleetPlacer = actorSystem.actorOf(Props(new MasterActor(shipsPlacer)), name = "fleet_placer")

  }

  override def afterEach() {
    actorSystem.shutdown
  }
}