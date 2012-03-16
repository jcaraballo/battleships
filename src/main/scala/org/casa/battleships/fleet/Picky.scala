package org.casa.battleships.fleet


case class Picky[T] private(xs: Seq[T])(partialOrdering: PartialOrdering[T]) {
  def +(s: T): Picky[T] = new Picky(Picky.internalAddition(xs, s)(partialOrdering))(partialOrdering)
}

object Picky {
  def picky[T](xs: T*)(implicit partialOrdering: PartialOrdering[T]): Picky[T] = {
    new Picky((Seq[T]() /: xs)((x: Seq[T], y: T) => internalAddition(x, y)(partialOrdering)))(partialOrdering)
  }

  private def internalAddition[T](xs: Seq[T], x: T)(partialOrdering: PartialOrdering[T]): Seq[T] =
    if (xs.exists(partialOrdering.lteq (x, _))) xs else xs.filterNot(partialOrdering.lteq(_, x)) :+ x
}