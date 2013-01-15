package org.casa.battleships.strategy.shooting.probabilistic

import scala.concurrent.duration._
import akka.pattern.ask
import org.scalatest.{BeforeAndAfterEach, FunSuite}
import akka.util.Timeout
import org.casa.battleships.Position._
import collection.immutable.Set
import testtools.fixtures.Examples._
import org.casa.battleships.Positions
import java.util.concurrent.TimeoutException
import scala.concurrent.{Future, Await}
import org.scalatest.matchers.ShouldMatchers
import org.casa.battleships.fleet.{Bag, FleetLocation, ShipLocation}
import org.mockito.Mockito._
import akka.actor._
import akka.testkit.TestActorRef
import org.casa.battleships.strategy.shooting.probabilistic.MasterActor.Response
import concurrent.duration.FiniteDuration

class MasterActorTest extends FunSuite with BeforeAndAfterEach with ShouldMatchers {
  val duration: FiniteDuration = 1.second
  implicit val timeout = Timeout(duration)
  var actorSystem: ActorSystem = _

  val fleetLocation1 = FleetLocation(Set(ShipLocation(Set(pos(1, 2), pos(2, 2)))))
  val fleetLocation2 = FleetLocation(Set(ShipLocation(Set(pos(4, 2), pos(5, 2)))))
  val fleetConfiguration = FleetConfiguration(someAvailability)

  test("Responds with the original if it is already complete") {
    val workerFactory: ActorFactory = mock(classOf[ActorFactory])
    val fleetLocation = mock(classOf[FleetLocation])
    when(fleetLocation.shipSizes).thenReturn(Bag(3))
    val fleetConfiguration = new FleetConfiguration(fleetLocation, someAvailability)

    val master = TestActorRef(new MasterActor(workerFactory)(Bag(3)))(actorSystem)

    val actualResponse = Await.result(master ? MasterActor.Request(Set(fleetConfiguration)), 1 seconds).asInstanceOf[Response]

    actualResponse should equal(MasterActor.Response(Set(fleetLocation)))

    verifyZeroInteractions(workerFactory)
  }

  test("Given one that no ships have been placed and there's one ship to be placed, delegates to worker and responds with a location per returned conf") {
    val configurationsToBeReturnedByTheMock = Set(
      FleetConfiguration(fleetLocation1, someOtherAvailability),
      FleetConfiguration(fleetLocation2, someOtherAvailability)
    )

    val workerFactory = factoryThatReturns(mockWorkerActor(fleetConfiguration, someShipSize, configurationsToBeReturnedByTheMock))

    val master = TestActorRef(new MasterActor(workerFactory)(Bag(someShipSize)), name = "master")(actorSystem)

    val future: Future[Any] = master ? MasterActor.Request(Set(fleetConfiguration))
    Await.result(future, 20 seconds) match {
      case response: MasterActor.Response => response should equal(MasterActor.Response(Set(fleetLocation1, fleetLocation2)))

      case unexpected => throw new IllegalStateException("Got unexpected response back" + unexpected)
    }
  }

  test("Times out when request takes longer than time out") {
    val workerFactory = factoryThatReturns(actorSystem.actorOf(Props(new WorkerActor(new ShipLocationMultiplyPlacer))))
    val master = actorSystem.actorOf(Props(new MasterActor(workerFactory)(classicBagOfShipSizes)), name = "worker")

    val future = master.ask(MasterActor.Request(Set(FleetConfiguration(Positions.createGrid(10)))))
    try {
      Await.result(future, 1 milli)
      fail("Should throw a TimeoutException")
    }
    catch {
      case e: TimeoutException => null // expected
    }
  }

  override protected def beforeEach() {
    actorSystem = ActorSystem("MySystem")
  }

  override def afterEach() {
    actorSystem.shutdown()
  }

  private def mockWorkerActor(expectedFleetConfiguration: FleetConfiguration, expectedShipSize: Int, toBeReplied: Set[FleetConfiguration]): ActorRef = {
    actorSystem.actorOf(Props(new Actor() {
      def receive = {
        case WorkerActor.Request(fc, ss) if (expectedFleetConfiguration == fc && expectedShipSize == ss) => {
          sender ! WorkerActor.Response(expectedFleetConfiguration, toBeReplied)
        }
        case somethingElse => sender ! akka.actor.Status.Failure(new AssertionError("Unexpected request: " + somethingElse))
      }
    }))
  }

  private def factoryThatReturns(worker: ActorRef): ActorFactory = {
    new ActorFactory {
      def create = {
        worker
      }
    }
  }
}