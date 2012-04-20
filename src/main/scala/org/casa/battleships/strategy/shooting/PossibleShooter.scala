package org.casa.battleships.strategy.shooting

import akka.actor.ActorRef
import akka.util.Timeout
import akka.pattern.ask

import org.casa.battleships.{ShotOutcome, Position}
import org.casa.battleships.ShotOutcome.Water
import probabilistic._
import org.casa.battleships.strategy.positionchoice.PositionChooser
import akka.dispatch.Future
import probabilistic.MasterActor.Response
import org.casa.battleships.fleet.{FleetLocation, Bag}

class PossibleShooter(chooser: PositionChooser, master: ActorRef, shipSizes: Bag[Int])(implicit timeout: Timeout) {
  def shoot(shootable: Set[Position], history: List[(Position, ShotOutcome.Value)]): Future[Option[Position]] = {
    val pastHits: Set[Position] = history.filterNot(_._2 == Water).map(_._1).toSet

    master.ask(MasterActor.Request(Set(FleetConfiguration(shootable ++ pastHits))))(timeout).map(untypedResponse => {
      val response = untypedResponse.asInstanceOf[Response]
      val allFleetLocations: Set[FleetLocation] = response.allFleetLocations
      val allCompatibleFleetLocations: Set[FleetLocation] = allFleetLocations.filter(fl => pastHits.subsetOf(fl.squares))
      val allShootablePositions: Set[Position] = allCompatibleFleetLocations.flatMap(_.squares)
      chooser.choose(allShootablePositions)
    }
    )
  }
}