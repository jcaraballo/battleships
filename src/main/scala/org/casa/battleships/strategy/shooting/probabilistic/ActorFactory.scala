package org.casa.battleships.strategy.shooting.probabilistic

import akka.actor.ActorRef

trait ActorFactory {
  def create: ActorRef
}