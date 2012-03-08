package org.casa.battleships.fleet

import org.scalatest.FunSuite
import org.casa.battleships.Position.pos
import org.junit.Assert.assertThat
import org.hamcrest.CoreMatchers.is
import org.scalatest.matchers.ShouldMatchers

class FleetLocationTest extends FunSuite with ShouldMatchers {
  val location1 = new ShipLocation(pos(1, 1), pos(2, 1))
  val location2 = new ShipLocation(pos(1, 1), pos(2, 1))

  test("can add ship locations using the '+' operator"){
    assertThat(new FleetLocation(Set()) + location1, is(new FleetLocation(Set(location1))))
    assertThat(new FleetLocation(Set(location1)) + location2, is(new FleetLocation(Set(location1, location2))))
  }

  test("Gives the sizes of the ships that have been allocated"){
    val size5Ship = new ShipLocation(pos(1, 1), pos(5, 1))
    val size4Ship = new ShipLocation(pos(1, 2), pos(4, 2))

    val fleetLocation = FleetLocation(Set(size5Ship, size4Ship))

    fleetLocation.shipSizes.sorted should equal((5 :: 4 :: Nil).sorted)
  }
}