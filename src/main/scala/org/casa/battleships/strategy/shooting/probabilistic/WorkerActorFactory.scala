package org.casa.battleships.strategy.shooting.probabilistic

import akka.actor.ActorRef

trait WorkerActorFactory {
  def create: ActorRef
}