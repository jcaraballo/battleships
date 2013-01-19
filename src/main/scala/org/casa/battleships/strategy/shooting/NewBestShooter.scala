package org.casa.battleships.strategy.shooting

import org.casa.battleships.strategy.positionchoice.PositionChooser
import org.casa.battleships.{ShotOutcome, Position}
import akka.actor.{PoisonPill, ActorSystem, Props}
import probabilistic.{ShipLocationMultiplyPlacer, WorkerActor, ActorFactory, MasterActor}
import akka.util.Timeout
import org.casa.battleships.strategy.shooting.Shooters._
import concurrent._
import java.util.concurrent.TimeoutException
import org.casa.battleships.fleet.Bag
import ExecutionContext.Implicits.global
import scala.concurrent.duration._


class NewBestShooter(chooser: PositionChooser, actorSystem2: ActorSystem, shipSizes: Bag[Int], timeoutDuration: FiniteDuration) extends Shooter {
  def shoot(shootable: Set[Position], history: List[(Position, ShotOutcome.Value)]): Option[Position] = {
//    val actorSystem = ActorSystem("MySystem")
//    try {
      val possibleShooter = new PossibleShooter(chooser, actorSystem.actorOf(Props(new MasterActor(new ActorFactory {
        def create = actorSystem.actorOf(Props(new WorkerActor(new ShipLocationMultiplyPlacer)))
      })(shipSizes))), shipSizes)(Timeout(timeoutDuration))
      val formerlyBestShooter: Shooter = bestShooter(chooser)

      val possibleShotFuture: Future[Option[Position]] = possibleShooter.shoot(shootable, history)


      val formerlyBestShot: Future[Option[Position]] = future {
        formerlyBestShooter.shoot(shootable, history)
      }

      try {
        val possibleShot: Option[Position] = Await.result(possibleShotFuture, timeoutDuration)
        possibleShot
      }
      catch {
        case e: TimeoutException => {
          actorSystem.actorSelection("*") ! PoisonPill
          if (formerlyBestShot.isCompleted) Await.result(formerlyBestShot, 0 seconds) else None
        }
      }
//    } finally {
//      actorSystem.shutdown()
//    }
  }
}