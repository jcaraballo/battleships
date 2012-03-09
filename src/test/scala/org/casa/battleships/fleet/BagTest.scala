package org.casa.battleships.fleet

import org.scalatest.FunSuite
import collection.mutable.Buffer
import org.scalatest.matchers.ShouldMatchers

class BagTest extends FunSuite with ShouldMatchers {

  test("Can go through it") {
    val elements = Buffer[Int]()
    for (i <- Bag(1, 1, 2, 4, 3)) {
      elements += i
    }

    elements.sorted should equal(List(1, 1, 2, 4, 3).sorted)
  }

  test("Has a sensible toString") {
    Bag(1, 1, 2, 4, 3).toString should equal("Bag(1, 1, 2, 4, 3)")
  }

  test("Order doesn't matter") {
    Bag(1, 2, 3) should equal(Bag(1, 3, 2))
    Bag(1, 2, 3) should equal(Bag(2, 1, 3))
    Bag(1, 2, 3) should equal(Bag(2, 3, 1))
  }

  test("Repetition does matter") {
    Bag(1, 2, 2) should not equal (Bag(1, 2))
    Bag(1, 2, 2) should not equal (Bag(1, 2, 2, 2))
  }

  test("Subtracts one element"){
    (Bag(1, 2, 2, 3) - 1) should equal (Bag(2, 2, 3))
    (Bag(1, 2, 2, 3) - 2) should equal (Bag(1, 2, 3))
  }
  
  test("Subtracts another bag") {
    (Bag(1, 2, 2, 3) -- Bag(1, 2)) should equal(Bag(2, 3))
  }
}