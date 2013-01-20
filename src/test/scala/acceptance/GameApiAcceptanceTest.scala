package acceptance

import org.scalatest.{BeforeAndAfterEach, FunSuite}
import dispatch._
import org.scalatest.matchers.ShouldMatchers
import org.casa.battleships.{Board, ApiServer}
import org.casa.battleships.fleet.Ship._
import org.casa.battleships.Position._
import org.casa.battleships.fleet.Fleet
import com.ning.http.client.RequestBuilder


class GameApiAcceptanceTest extends FunSuite with ShouldMatchers with BeforeAndAfterEach {
  var apiServer: ApiServer = _

  test("creates games") {
    Http(go("/game").POST << "bob,tim" OK as.String)().trim should be("1,bob")
    Http(go("/game").POST << "bob,tim" OK as.String)().trim should be("2,bob")
  }

  test("plays game") {
    Http(go("/game").POST << "bob,tim" OK as.String)().trim should be("1,bob")
    Http(go("/game/1/shot").POST << "bob,1,5" OK as.String)().trim should be("Hit,tim")
    Http(go("/game/1/shot").POST << "tim,1,5" OK as.String)().trim should be("Hit,bob")
    Http(go("/game/1/shot").POST << "bob,2,5" OK as.String)().trim should be("Sunk,tim")
    Http(go("/game/1/shot").POST << "tim,3,5" OK as.String)().trim should be("Water,bob")
  }

  private def go(path: String): RequestBuilder = {
    url("http://localhost:8080" + path)
  }

  override def beforeEach() {
    apiServer = new ApiServer(8080, new Board(10, new Fleet(
      immaculateShip(pos(1, 1), pos(5, 1)),
      immaculateShip(pos(1, 2), pos(4, 2)),
      immaculateShip(pos(1, 3), pos(3, 3)),
      immaculateShip(pos(1, 4), pos(3, 4)),
      immaculateShip(pos(1, 5), pos(2, 5))))).start()
  }

  override def afterEach() {
    apiServer.stop()
  }
}