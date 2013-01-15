package org.casa.battleships.strategy.shooting.probabilistic

import scala.concurrent.duration._
import scala.concurrent.Await
import akka.pattern.ask
import org.scalatest.{BeforeAndAfterEach, FunSuite}
import akka.util.Timeout
import akka.actor.{ActorRef, Props, ActorSystem}
import org.casa.battleships.Position._
import org.casa.battleships.Positions._
import collection.immutable.Set
import org.mockito.Mockito._
import org.casa.battleships.fleet.ShipLocation
import org.scalatest.matchers.ShouldMatchers

class WorkerActorTest extends FunSuite with BeforeAndAfterEach with ShouldMatchers {
  val duration: FiniteDuration = 1.second
  implicit val timeout = Timeout(duration)
  var actorSystem: ActorSystem = _
  var worker: ActorRef = _

  val shipPlacer = mock(classOf[ShipLocationMultiplyPlacer])

  test("Delegates to ShipLocationMultiplyPlacer") {
    val shipLocation1 = new ShipLocation(pos(1, 1), pos(5, 1))
    val shipLocation2 = new ShipLocation(pos(6, 3), pos(8, 3))
    val shipLocations = Set(shipLocation1, shipLocation2)
    val availability = createGrid(10)

    when(shipPlacer.findAllShipLocations(2, availability)).thenReturn(shipLocations)

    val fleetConfiguration = mock(classOf[FleetConfiguration])
    val fleetConfiguration_plusShipLocation1 = mock(classOf[FleetConfiguration])
    val fleetConfiguration_plusShipLocation2 = mock(classOf[FleetConfiguration])

    when(fleetConfiguration.availability).thenReturn(availability)
    when(fleetConfiguration + shipLocation1).thenReturn(fleetConfiguration_plusShipLocation1)
    when(fleetConfiguration + shipLocation2).thenReturn(fleetConfiguration_plusShipLocation2)

    val future = worker.ask(WorkerActor.Request(fleetConfiguration, 2))
    val response = Await.result(future, duration).asInstanceOf[WorkerActor.Response]

    response should equal(WorkerActor.Response(fleetConfiguration, Set(
      fleetConfiguration_plusShipLocation1,
      fleetConfiguration_plusShipLocation2
    )))
  }

  override def beforeEach() {
    actorSystem = ActorSystem("MySystem")
    worker = actorSystem.actorOf(Props(new WorkerActor(shipPlacer)), name = "worker")
  }

  override def afterEach() {
    actorSystem.shutdown
  }
}