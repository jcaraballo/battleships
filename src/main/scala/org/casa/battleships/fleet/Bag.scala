package org.casa.battleships.fleet

import collection.immutable.Map

final class Bag[T] private(override val toList: List[T], val toMap: Map[T, Int]) extends Iterable[T] {

  def iterator = toList.iterator

  def -(t: T): Bag[T] = {
    Bag.fromList(removeFirstOccurrence(toList, t))
  }

  def --(other: Bag[T]): Bag[T] = {
    Bag.fromList((toList /: other)(removeFirstOccurrence(_, _)))
  }

  override def equals(other: Any): Boolean = other match {
    case that: Bag[_] => toMap == that.toMap
    case _ => false
  }

  override def hashCode(): Int = toMap.hashCode()

  override def toString(): String = toList.mkString("Bag(", ", ", ")")

  private def removeFirstOccurrence(ts: List[T], t: T): List[T] = {
    val indexOfFirstOccurrence = ts.indexOf(t)
    if (indexOfFirstOccurrence == -1) ts
    else ts.take(indexOfFirstOccurrence) ::: ts.takeRight(ts.size - indexOfFirstOccurrence - 1)
  }
}

object Bag {
  def fromList[T](asList: List[T]): Bag[T] = {
    val asMap: Map[T, Int] = (Map[T, Int]() /: asList)((acc, t) => acc.updated(t, acc.getOrElse(t, 0) + 1))
    new Bag[T](asList, asMap)
  }

  def apply[T](ts: T*) = {
    fromList(ts.toList)
  }
}