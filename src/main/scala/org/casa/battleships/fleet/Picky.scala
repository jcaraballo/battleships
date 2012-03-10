package org.casa.battleships.fleet


import collection.immutable.Set

case class Picky[T] private(sets: Set[Set[T]]) {
  def +(s: Set[T]): Picky[T] = Picky(Picky.internalAddition(this.sets, s))
}

object Picky {
  def apply[T](sets: Set[T]*): Picky[T] = Picky((Set[Set[T]]() /: sets)(internalAddition(_, _)))

  private def internalAddition[T](c: Set[Set[T]], s: Set[T]): Set[Set[T]] =
    if (c.exists(s subsetOf _)) c else c.filterNot(_ subsetOf s) + s
}