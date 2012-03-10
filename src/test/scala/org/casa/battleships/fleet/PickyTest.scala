package org.casa.battleships.fleet

import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers

class PickyTest extends FunSuite with ShouldMatchers {
  test("Ignores additions of elements that are subsets of present elements") {
    Picky(Set(1, 2)) + Set(1) should equal(Picky(Set(1, 2)))
  }

  test("Adding an element replaces its subsets") {
    Picky(Set(1)) + Set(1, 2) should equal(Picky(Set(1, 2)))
  }

  test("Adding an element that doesn't subset and isn't subset of any of the elements adds it") {
    Picky(Set(1, 3)) + Set(1, 2) should equal(Picky(Set(1, 3), Set(1, 2)))
  }

  test("Elements that are subsets of other elements are disregarded") {
    Picky(Set(1, 2), (Set(1))) should equal(Picky(Set(1, 2)))
  }
}