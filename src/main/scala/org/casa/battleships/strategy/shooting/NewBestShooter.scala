package org.casa.battleships.strategy.shooting

import org.casa.battleships.strategy.positionchoice.PositionChooser
import org.casa.battleships.{ShotOutcome, Position}
import akka.actor.{ActorSystem, Props}
import probabilistic.{ShipLocationMultiplyPlacer, WorkerActor, WorkerActorFactory, MasterActor}
import akka.util.Timeout
import org.casa.battleships.strategy.shooting.Shooters._
import concurrent._
import java.util.concurrent.TimeoutException
import org.casa.battleships.fleet.Bag
import ExecutionContext.Implicits.global
import scala.concurrent.duration._


class NewBestShooter(chooser: PositionChooser, actorSystem: ActorSystem, shipSizes: Bag[Int], timeoutDuration: FiniteDuration) extends Shooter {
  def shoot(shootable: Set[Position], history: List[(Position, ShotOutcome.Value)]): Option[Position] = {
    val masterActor = actorSystem.actorOf(Props(new MasterActor(new WorkerActorFactory {
      def create = actorSystem.actorOf(Props(new WorkerActor(new ShipLocationMultiplyPlacer)))
    })(shipSizes)))

    val possibleShooter = new PossibleShooter(chooser, masterActor, shipSizes)(Timeout(timeoutDuration))
    try {
      val possibleShotFuture: Future[Option[Position]] = possibleShooter.shoot(shootable, history)

      val formerlyBestShotFuture: Future[Option[Position]] = future {
        bestShooter(chooser).shoot(shootable, history)
      }

      try {
        Await.result(possibleShotFuture, timeoutDuration)
      }
      catch {
        case e: TimeoutException => {
          if (formerlyBestShotFuture.isCompleted) Await.result(formerlyBestShotFuture, 0 seconds) else None
        }
      }
    } finally {
      actorSystem.stop(masterActor)
    }
  }
}