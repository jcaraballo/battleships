package org.casa.battleships.strategy.shooting.probabilistic

import akka.util.duration._
import akka.pattern.ask
import org.scalatest.{BeforeAndAfterEach, FunSuite}
import akka.util.{Duration, Timeout}
import org.casa.battleships.Position._
import collection.immutable.Set
import testtools.fixtures.Examples._
import org.casa.battleships.fleet.{FleetLocation, ShipLocation}
import org.casa.battleships.Positions
import testtools.fixtures.Examples
import java.util.concurrent.TimeoutException
import akka.actor.{Actor, ActorRef, Props, ActorSystem}
import akka.dispatch.{Future, Await}
import org.scalatest.matchers.ShouldMatchers

class MasterActorTest extends FunSuite with BeforeAndAfterEach with ShouldMatchers {
  val duration: Duration = 1 second
  implicit val timeout = Timeout(duration)

  var actorSystem: ActorSystem = _
  var worker: ActorRef = _
  var master: ActorRef = _

  val fleetLocation1 = FleetLocation(Set(ShipLocation(Set(pos(1, 2), pos(2, 2)))))
  val fleetLocation2 = FleetLocation(Set(ShipLocation(Set(pos(4, 2), pos(4, 2)))))
  val fleetConfiguration = FleetConfiguration(someAvailability)

  test("Given one fleet conf and one ship size, delegates to worker and returns a location per returned conf"){
    val configurationsToBeReturnedByTheMock = Set(
      FleetConfiguration(fleetLocation1, someOtherAvailability),
      FleetConfiguration(fleetLocation2, someOtherAvailability)
    )
    val fakeWorker = mockWorkerActor(fleetConfiguration, someShipSize, configurationsToBeReturnedByTheMock)

    master = actorSystem.actorOf(Props(new MasterActor(fakeWorker)(someShipSize::Nil)), name = "master")

    val future: Future[Any] = master ? MasterActor.Request(Set(fleetConfiguration))
    Await.result(future, duration) match {
      case response: MasterActor.Response => response should equal (MasterActor.Response(Set(fleetLocation1, fleetLocation2)))

      case unexpected => throw new IllegalStateException("Got unexpected response back" + unexpected)
    }
  }

  test("Times out when request takes longer than time out"){
    worker = actorSystem.actorOf(Props(new WorkerActor(new ShipLocationMultiplyPlacer)))
    master = actorSystem.actorOf(Props(new MasterActor(worker)(classicListOfShipSizes)), name = "worker")

    val future = master.ask(MasterActor.Request(Set(FleetConfiguration(Positions.createGrid(10)))))
    try {
      Await.result(future, 1 milli)
      fail("Should throw a TimeoutException")
    }
    catch {
      case e: TimeoutException => null // expected
    }
  }

  override def beforeEach() {
    actorSystem = ActorSystem("MySystem")
  }

  override def afterEach() {
    actorSystem.shutdown
  }

  private def mockWorkerActor(expectedFleetConfiguration: FleetConfiguration, expectedShipSize: Int, toBeReplied: Set[FleetConfiguration]): ActorRef = {
    actorSystem.actorOf(Props(new Actor() {
      protected def receive = {
        case WorkerActor.Request(fc, ss) if (expectedFleetConfiguration == fc && expectedShipSize == ss) => sender ! WorkerActor.Response(toBeReplied)
        case somethingElse => sender ! akka.actor.Status.Failure(new AssertionError("Unexpected request: " + somethingElse))
      }
    }))
  }
}