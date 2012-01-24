package org.casa.battleships.fleet

import org.scalatest.FunSuite
import org.casa.battleships.Position.pos
import org.junit.Assert.assertThat
import org.hamcrest.CoreMatchers.is

class FleetLocationTest extends FunSuite {
  val location1 = new ShipLocation(pos(1, 1), pos(2, 1))
  val location2 = new ShipLocation(pos(1, 1), pos(2, 1))

  test("can add ship locations using the '+' operator"){
    assertThat(new FleetLocation(Set()) + location1, is(new FleetLocation(Set(location1))))
    assertThat(new FleetLocation(Set(location1)) + location2, is(new FleetLocation(Set(location1, location2))))
  }
}