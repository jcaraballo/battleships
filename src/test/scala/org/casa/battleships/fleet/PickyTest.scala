package org.casa.battleships.fleet

import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers
import Picky.picky

class PickyTest extends FunSuite with ShouldMatchers {
  implicit def subsetOrdering[A] = new PartialOrdering[Set[A]] {
    def tryCompare(x: Set[A], y: Set[A]) =
      if (x == y) Some(0)
      else if (x subsetOf y) Some(-1)
      else if (y subsetOf x) Some(1)
      else None

    def lteq(x: Set[A], y: Set[A]) =
      this.tryCompare(x, y).map(_ <= 0).getOrElse(false)
  }

  test("Ignores additions of elements that are subsets of present elements") {
    val actual = picky(Set(1, 2)) + Set(1)
    actual should equal(picky(Set(1, 2)))
  }

  test("Adding an element replaces its subsets") {
    picky(Set(1)) + Set(1, 2) should equal(picky(Set(1, 2)))
  }

  test("Adding an element that doesn't subset and isn't subset of any of the elements adds it") {
    picky(Set(1, 3)) + Set(1, 2) should equal(picky(Set(1, 3), Set(1, 2)))
  }

  test("Elements that are subsets of other elements are disregarded") {
    picky(Set(1, 2), (Set(1))) should equal(picky(Set(1, 2)))
  }
}