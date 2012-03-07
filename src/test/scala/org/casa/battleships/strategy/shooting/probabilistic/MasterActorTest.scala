package org.casa.battleships.strategy.shooting.probabilistic

import org.junit.Assert._
import akka.util.duration._
import akka.pattern.ask
import org.scalatest.{BeforeAndAfterEach, FunSuite}
import akka.util.{Duration, Timeout}
import org.casa.battleships.Position._
import org.hamcrest.CoreMatchers._
import collection.immutable.Set
import testtools.fixtures.Examples._
import org.casa.battleships.fleet.{FleetLocation, ShipLocation}
import org.casa.battleships.{Positions, Position}
import org.casa.battleships.Positions.createGrid
import testtools.fixtures.Builders._
import testtools.fixtures.Examples
import java.util.concurrent.TimeoutException
import akka.actor.{Actor, ActorRef, Props, ActorSystem}
import grizzled.slf4j.Logger
import akka.dispatch.{Future, Await}
import org.scalatest.matchers.ShouldMatchers

class MasterActorTest extends FunSuite with BeforeAndAfterEach with ShouldMatchers {
  val logger = Logger(classOf[MasterActorTest])

  val fleetLocation1 = FleetLocation(Set(ShipLocation(Set(pos(1, 2), pos(2, 2)))))
  val fleetLocation2 = FleetLocation(Set(ShipLocation(Set(pos(4, 2), pos(4, 2)))))
  val fleetConfiguration = FleetConfiguration(createGrid(10))
  val someAvailability = Set(pos(9, 9))
  val someOtherAvailability = Set(pos(10, 10))

  test("Given one fleet conf and one ship size, delegates to worker and returns a location per returned conf"){
    val configurationsToBeReturnedByTheMock = Set(
      FleetConfiguration(fleetLocation1, someAvailability),
      FleetConfiguration(fleetLocation2, someOtherAvailability)
    )
    val fakeWorker = mockWorkerActor(fleetConfiguration, configurationsToBeReturnedByTheMock)

    master = actorSystem.actorOf(Props(new MasterActor(fakeWorker)), name = "master")

    val future: Future[Any] = master ? MasterActor.SelfRequest(2 :: Nil, Set(fleetConfiguration))
    Await.result(future, duration) match {
      case response: MasterActor.Response => response should equal (MasterActor.Response(Set(fleetLocation1, fleetLocation2)))

      case unexpected => throw new IllegalStateException("Got unexpected response back" + unexpected)
    }
  }  

  test("No possible location when no available space") {
    assertThat(findThemAll(someListOfShipSizes, Set()), is(Set[FleetLocation]()))
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
    val future = master.ask(MasterActor.Request(Examples.classicListOfShipSizes, Positions.createGrid(10)))
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
  var worker: ActorRef = _
  var master: ActorRef = _

  private def findThemAll(shipSizes: List[Int], availability: Set[Position]): Set[FleetLocation] = {
    val future = master.ask(MasterActor.Request(shipSizes, availability))
    Await.result(future, duration) match {
      case response: MasterActor.Response => response.allFleetLocations

      case unexpected => throw new IllegalStateException("Got unexpected response back" + unexpected)
    }
  }

  override def beforeEach() {
    actorSystem = ActorSystem("MySystem")
    worker = actorSystem.actorOf(Props(new WorkerActor(new ShipLocationMultiplyPlacer)))
    master = actorSystem.actorOf(Props(new MasterActor(worker)), name = "fleet_placer")

  }

  override def afterEach() {
    actorSystem.shutdown
  }

  private def mockWorkerActor(expected: FleetConfiguration, toBeReplied: Set[FleetConfiguration]): ActorRef = {
    actorSystem.actorOf(Props(new Actor() {
      protected def receive = {
        case WorkerActor.Request(fc, 2) if expected == fc => sender ! WorkerActor.Response(toBeReplied)
        case somethingElse => sender ! akka.actor.Status.Failure(new AssertionError("Unexpected request: " + somethingElse))
      }
    }))
  }
}