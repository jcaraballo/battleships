package org.casa.battleships.fleet

import org.scalatest.FunSuite
import org.casa.battleships.Position.pos
import org.junit.Assert.assertThat
import org.hamcrest.CoreMatchers.is
import org.scalatest.matchers.ShouldMatchers
import org.mockito.Mockito._

class FleetLocationTest extends FunSuite with ShouldMatchers {
  val location1 = new ShipLocation(pos(1, 1), pos(2, 1))
  val location2 = new ShipLocation(pos(1, 2), pos(2, 2))

  test("can add ship locations using the '+' operator") {
    assertThat(new FleetLocation(Set()) + location1, is(new FleetLocation(Set(location1))))
    assertThat(new FleetLocation(Set(location1)) + location2, is(new FleetLocation(Set(location1, location2))))
  }

  test("Gives the sizes of the ships that have been allocated") {
    val size5Ship = new ShipLocation(pos(1, 1), pos(5, 1))
    val size4Ship = new ShipLocation(pos(1, 2), pos(4, 2))

    val fleetLocation = FleetLocation(Set(size5Ship, size4Ship))

    fleetLocation.shipSizes should equal(Bag(5, 4))
  }

  test("One FC subsets another iff their sets of ship locations do") {
    val locations1: Set[ShipLocation] = mockSetOfShipLocation
    val locations2: Set[ShipLocation] = mockSetOfShipLocation
    when(locations1.subsetOf(locations2)).thenReturn(true)
    FleetLocation(locations1) ⊆ (FleetLocation(locations2)) should be(true)

    val locations3: Set[ShipLocation] = mockSetOfShipLocation
    val locations4: Set[ShipLocation] = mockSetOfShipLocation
    when(locations3.subsetOf(locations4)).thenReturn(false)
    FleetLocation(locations3) ⊆ (FleetLocation(locations4)) should be(false)
  }

  private def mockSetOfShipLocation: Set[ShipLocation] = {
    mock(classOf[Set[ShipLocation]])
  }
}