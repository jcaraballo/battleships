package org.casa.battleships.strategy.shooting

import akka.actor.{ActorSystem, Props, Actor}
import akka.pattern.ask
import org.mockito.Mockito._
import akka.util.Timeout
import scala.concurrent.duration._
import probabilistic.{FleetConfiguration, MasterActor}
import org.casa.battleships.Position.pos
import concurrent.{Await, Future}
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.{BeforeAndAfterEach, FunSuite}
import org.casa.battleships.Positions._
import org.casa.battleships.{ShotOutcome, Position}
import org.casa.battleships.ShotOutcome.Hit
import org.casa.battleships.strategy.positionchoice.PositionChooser
import scala.Option
import org.casa.battleships.fleet.{ShipLocation, Bag, FleetLocation}
import testtools.fixtures.Examples._

class PossibleShooterTest extends FunSuite with BeforeAndAfterEach with ShouldMatchers {
  var actorSystem: ActorSystem = _
  var chooser: PositionChooser = _

  implicit val timeout = Timeout(1 second)

  case class HasItBeenCalledExactlyOnce()

  test("Shoots none when master reponds with no possible fleet locations") {
    val availability: Set[Position] = createGrid(10)

    val master = actorSystem.actorOf(Props(new Actor {
      var calledTimes: Int = 0

      def receive = {
        case MasterActor.Request(fleetConfigurations) if fleetConfigurations == Set(FleetConfiguration(availability)) => {
          calledTimes += 1
          sender ! MasterActor.Response(Set[FleetLocation]())
        }
        case HasItBeenCalledExactlyOnce() => sender ! (calledTimes == 1)
        case e => akka.actor.Status.Failure(new AssertionError("Unexpected message sent to MasterActor: " + e))
      }
    }), name = "master")

    when(chooser.choose(Set[Position]())).thenReturn(None)

    val possibleShooter: PossibleShooter = new PossibleShooter(chooser, master, Bag(3))

    val shoot: Future[Option[Position]] = possibleShooter.shoot(availability, List())
    val result: Option[Position] = Await.result(shoot, 1 second)
    result should be(None)

    val fut: Future[Any] = master ? HasItBeenCalledExactlyOnce()
    Await.result(fut, 1 second).asInstanceOf[Boolean] should be(true)
  }

  test("When master reponds with one fleet location, shoots one of the squares of said fleet location, for no history") {
    val availability: Set[Position] = createGrid(10)

    val master = actorSystem.actorOf(Props(new Actor {
      var calledTimes: Int = 0

      def receive = {
        case MasterActor.Request(fleetConfigurations) if fleetConfigurations == Set(FleetConfiguration(availability)) => {
          calledTimes += 1
          sender ! MasterActor.Response(Set[FleetLocation](someFleetLocation))
        }
        case HasItBeenCalledExactlyOnce() => sender ! (calledTimes == 1)
        case e => akka.actor.Status.Failure(new AssertionError("Unexpected message sent to MasterActor: " + e))
      }
    }), name = "master")

    when(chooser.choose(someFleetLocation.squares)).thenReturn(Some(pos(10, 10)))

    val possibleShooter: PossibleShooter = new PossibleShooter(chooser, master, Bag(3))

    val shoot: Future[Option[Position]] = possibleShooter.shoot(availability, List())
    val result: Option[Position] = Await.result(shoot, 1 second)
    result should be(Some(pos(10, 10)))

    val fut: Future[Any] = master ? HasItBeenCalledExactlyOnce()
    Await.result(fut, 1 second).asInstanceOf[Boolean] should be(true)
  }

  test("Shoots one of the squares of the fleet locations responded by master, for no history") {
    val availability: Set[Position] = createGrid(10)

    val master = actorSystem.actorOf(Props(new Actor {
      var calledTimes: Int = 0

      def receive = {
        case MasterActor.Request(fleetConfigurations) if fleetConfigurations == Set(FleetConfiguration(availability)) => {
          calledTimes += 1
          sender ! MasterActor.Response(Set[FleetLocation](someFleetLocation, someOtherFleetLocation))
        }
        case HasItBeenCalledExactlyOnce() => sender ! (calledTimes == 1)
        case e => akka.actor.Status.Failure(new AssertionError("Unexpected message sent to MasterActor: " + e))
      }
    }), name = "master")

    when(chooser.choose(
      someFleetLocation.squares ++ someOtherFleetLocation.squares
    )).thenReturn(Some(pos(10, 10)))

    val possibleShooter: PossibleShooter = new PossibleShooter(chooser, master, Bag(3))

    val shoot: Future[Option[Position]] = possibleShooter.shoot(availability, List())
    val result: Option[Position] = Await.result(shoot, 1 second)
    result should be(Some(pos(10, 10)))

    val fut: Future[Any] = master ? HasItBeenCalledExactlyOnce()
    Await.result(fut, 1 second).asInstanceOf[Boolean] should be(true)
  }

  test("Sends master availability=shootable+[hit or sunk in history] and shoots one of the squares of a responded fleet location compatible with the history") {
    val history: List[(Position, ShotOutcome.Value)] = (pos(2, 1), Hit) ::(pos(1, 1), Hit) :: Nil
    val availability: Set[Position] = createGrid(10)
    val shootable: Set[Position] = availability - pos(1, 1) - pos(2, 1)
    val compatibleFleetLocation = FleetLocation(Set(
      new ShipLocation(Set(pos(1, 1), pos(2, 1))),
      new ShipLocation(pos(6, 6), pos(10, 6)),
      new ShipLocation(pos(6, 7), pos(8, 7))
    ))
    val incompatibleFleetLocation = FleetLocation(Set(
      new ShipLocation(pos(6, 9), pos(10, 9)),
      new ShipLocation(pos(6, 10), pos(8, 10))
    ))

    val master = actorSystem.actorOf(Props(new Actor {
      var calledTimes: Int = 0

      def receive = {
        case MasterActor.Request(fleetConfigurations) if fleetConfigurations == Set(FleetConfiguration(availability)) => {
          calledTimes += 1
          sender ! MasterActor.Response(Set[FleetLocation](compatibleFleetLocation, incompatibleFleetLocation))
        }
        case HasItBeenCalledExactlyOnce() => sender ! (calledTimes == 1)
        case e => akka.actor.Status.Failure(new AssertionError("Unexpected message sent to MasterActor: " + e))
      }
    }), name = "master")

    when(chooser.choose(
      compatibleFleetLocation.squares
    )).thenReturn(Some(pos(10, 10)))

    val possibleShooter: PossibleShooter = new PossibleShooter(chooser, master, Bag(3))

    val shoot: Future[Option[Position]] = possibleShooter.shoot(shootable, history)
    val result: Option[Position] = Await.result(shoot, 1 second)
    result should be(Some(pos(10, 10)))

    val fut: Future[Any] = master ? HasItBeenCalledExactlyOnce()
    Await.result(fut, 1 second).asInstanceOf[Boolean] should be(true)
  }

  override protected def beforeEach() {
    actorSystem = ActorSystem("MySystem")
    chooser = mock(classOf[PositionChooser])
  }

  override def afterEach() {
    actorSystem.shutdown()
  }
}