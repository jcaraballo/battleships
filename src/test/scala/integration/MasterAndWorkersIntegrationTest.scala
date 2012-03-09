package integration

import akka.util.duration._
import akka.pattern.ask
import org.scalatest.{BeforeAndAfterEach, FunSuite}
import akka.util.{Duration, Timeout}
import org.casa.battleships.Position._
import collection.immutable.Set
import testtools.fixtures.Examples._
import org.casa.battleships.fleet.{FleetLocation, ShipLocation}
import org.casa.battleships.{Positions, Position}
import testtools.fixtures.Builders._
import akka.actor.{ActorRef, Props, ActorSystem}
import akka.dispatch.Await
import org.scalatest.matchers.ShouldMatchers
import org.casa.battleships.strategy.shooting.probabilistic.{ShipLocationMultiplyPlacer, WorkerActor, MasterActor, FleetConfiguration}

class MasterAndWorkersIntegrationTest extends FunSuite with BeforeAndAfterEach with ShouldMatchers {
  val duration: Duration = 1 second
  implicit val timeout = Timeout(duration)

  var actorSystem: ActorSystem = _
  var worker: ActorRef = _
  var master: ActorRef = _

  val fleetLocation1 = FleetLocation(Set(ShipLocation(Set(pos(1, 2), pos(2, 2)))))
  val fleetLocation2 = FleetLocation(Set(ShipLocation(Set(pos(4, 2), pos(4, 2)))))
  val fleetConfiguration = FleetConfiguration(someAvailability)

  test("All possible fleets for 2 ships of 2 squares in grid") {
    val horizontalShips = Set(
      new ShipLocation(pos(1, 1), pos(2, 1)),
      new ShipLocation(pos(1, 2), pos(2, 2)))

    val verticalShips = Set(
      new ShipLocation(pos(1, 1), pos(1, 2)),
      new ShipLocation(pos(2, 1), pos(2, 2))
    )

    findThemAll(2 :: 2 :: Nil, Positions.createGrid(2)) should equal(Set(
      new FleetLocation(horizontalShips), new FleetLocation(verticalShips)
    ))
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

    findThemAll(5 :: Nil, available) should equal(Set(
      new FleetLocation(Set(ShipLocation(Set(pos(1, 10), pos(2, 10), pos(3, 10), pos(4, 10), pos(5, 10)))))
    ))
  }

  override def beforeEach() {
    actorSystem = ActorSystem("MySystem")
  }

  override def afterEach() {
    actorSystem.shutdown
  }

  private def findThemAll(shipSizes: List[Int], availability: Set[Position]): Set[FleetLocation] = {
    worker = actorSystem.actorOf(Props(new WorkerActor(new ShipLocationMultiplyPlacer)))
    master = actorSystem.actorOf(Props(new MasterActor(worker)(shipSizes)), name = "master")
    val future = master.ask(MasterActor.Request(Set(FleetConfiguration(availability))))
    Await.result(future, duration) match {
      case response: MasterActor.Response => response.allFleetLocations

      case unexpected => throw new IllegalStateException("Got unexpected response back" + unexpected)
    }
  }
}