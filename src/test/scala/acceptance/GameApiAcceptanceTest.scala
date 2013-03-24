package acceptance

import org.scalatest.{BeforeAndAfterEach, FunSuite}
import dispatch._
import org.scalatest.matchers.ShouldMatchers
import org.casa.battleships._
import org.casa.battleships.fleet.Ship._
import org.casa.battleships.Position._
import org.casa.battleships.fleet.Fleet
import com.ning.http.client.RequestBuilder
import org.casa.battleships.strategy.shooting.OneOneShooter


class GameApiAcceptanceTest extends FunSuite with ShouldMatchers with BeforeAndAfterEach {
  var apiServer: ApiServer = _

  test("creates games") {
    Http(go("/game").POST << "Bob,Tim" OK as.String)().trim should be("1,Bob")
    Http(go("/game").POST << "Bob,Tim" OK as.String)().trim should be("2,Bob")
  }

  test("plays a game") {
    Http(go("/game").POST << "Bob,Tim" OK as.String)().trim should be("1,Bob")
    Http(go("/game/1/history") OK as.String)().trim should be("")

    Http(go("/game/1/dashboard/Tim") OK as.String)().trim should be(
      """
        |  Bob                      Tim                  .
        |  1 2 3 4 5 6 7 8 9 0      1 2 3 4 5 6 7 8 9 0  .
        |  ~~~~~~~~~~~~~~~~~~~      ~~~~~~~~~~~~~~~~~~~  .
        |1{                   }1  1{< - - - >          }1.
        |2{                   }2  2{< - - >            }2.
        |3{                   }3  3{< - >              }3.
        |4{                   }4  4{< - >              }4.
        |5{                   }5  5{< >                }5.
        |6{                   }6  6{                   }6.
        |7{                   }7  7{                   }7.
        |8{                   }8  8{                   }8.
        |9{                   }9  9{                   }9.
        |0{                   }0  0{                   }0.
        |  ~~~~~~~~~~~~~~~~~~~      ~~~~~~~~~~~~~~~~~~~  .
        |  1 2 3 4 5 6 7 8 9 0      1 2 3 4 5 6 7 8 9 0  .""".stripMargin.filter(_ != '.').trim)

    Http(go("/game/1/dashboard/Bob") OK as.String)().trim should be(
      """
        |  Tim                      Bob                  .
        |  1 2 3 4 5 6 7 8 9 0      1 2 3 4 5 6 7 8 9 0  .
        |  ~~~~~~~~~~~~~~~~~~~      ~~~~~~~~~~~~~~~~~~~  .
        |1{                   }1  1{< - - - >          }1.
        |2{                   }2  2{< - - >            }2.
        |3{                   }3  3{< - >              }3.
        |4{                   }4  4{< - >              }4.
        |5{                   }5  5{< >                }5.
        |6{                   }6  6{                   }6.
        |7{                   }7  7{                   }7.
        |8{                   }8  8{                   }8.
        |9{                   }9  9{                   }9.
        |0{                   }0  0{                   }0.
        |  ~~~~~~~~~~~~~~~~~~~      ~~~~~~~~~~~~~~~~~~~  .
        |  1 2 3 4 5 6 7 8 9 0      1 2 3 4 5 6 7 8 9 0  .""".stripMargin.filter(_ != '.').trim)

    Http(go("/game/1/shot").POST << "Bob,1,5" OK as.String)().trim should be("Hit,Tim")
    Http(go("/game/1/shot").POST << "Tim,1,5" OK as.String)().trim should be("Hit,Bob")

    Http(go("/game/1/history") OK as.String)().trim should be("" +
      "Bob: (1, 5) => Hit\n" +
      "Tim: (1, 5) => Hit")

    Http(go("/game/1/shot").POST << "Bob,2,5" OK as.String)().trim should be("Sunk,Tim")
    Http(go("/game/1/shot").POST << "Tim,3,5" OK as.String)().trim should be("Water,Bob")

    Http(go("/game/1/dashboard/Tim") OK as.String)().trim should be(
      """
        |  Bob                      Tim                  .
        |  1 2 3 4 5 6 7 8 9 0      1 2 3 4 5 6 7 8 9 0  .
        |  ~~~~~~~~~~~~~~~~~~~      ~~~~~~~~~~~~~~~~~~~  .
        |1{                   }1  1{< - - - >          }1.
        |2{                   }2  2{< - - >            }2.
        |3{                   }3  3{< - >              }3.
        |4{                   }4  4{< - >              }4.
        |5{*   ·              }5  5{* *                }5.
        |6{                   }6  6{                   }6.
        |7{                   }7  7{                   }7.
        |8{                   }8  8{                   }8.
        |9{                   }9  9{                   }9.
        |0{                   }0  0{                   }0.
        |  ~~~~~~~~~~~~~~~~~~~      ~~~~~~~~~~~~~~~~~~~  .
        |  1 2 3 4 5 6 7 8 9 0      1 2 3 4 5 6 7 8 9 0  .""".stripMargin.filter(_ != '.').trim)
    Http(go("/game/1/dashboard/Bob") OK as.String)().trim should be(
      """
        |  Tim                      Bob                  .
        |  1 2 3 4 5 6 7 8 9 0      1 2 3 4 5 6 7 8 9 0  .
        |  ~~~~~~~~~~~~~~~~~~~      ~~~~~~~~~~~~~~~~~~~  .
        |1{                   }1  1{< - - - >          }1.
        |2{                   }2  2{< - - >            }2.
        |3{                   }3  3{< - >              }3.
        |4{                   }4  4{< - >              }4.
        |5{* *                }5  5{* > ·              }5.
        |6{                   }6  6{                   }6.
        |7{                   }7  7{                   }7.
        |8{                   }8  8{                   }8.
        |9{                   }9  9{                   }9.
        |0{                   }0  0{                   }0.
        |  ~~~~~~~~~~~~~~~~~~~      ~~~~~~~~~~~~~~~~~~~  .
        |  1 2 3 4 5 6 7 8 9 0      1 2 3 4 5 6 7 8 9 0  .""".stripMargin.filter(_ != '.').trim)
    Http(go("/game/1/history") OK as.String)().trim should be("" +
      "Bob: (1, 5) => Hit\n" +
      "Tim: (1, 5) => Hit\n" +
      "Bob: (2, 5) => Sunk\n" +
      "Tim: (3, 5) => Water")
  }

  test("Computer plays using /game web service") {
    val shooter: OneOneShooter = new OneOneShooter()

    Http(go("/game").POST << "Bob,Computer" OK as.String)().trim should be("1,Bob")
    Http(go("/game/1/shot").POST << "Bob,1,5" OK as.String)().trim should be("Hit,Computer")

    val history: Array[String] = Http(go("/game/1/history") OK as.String)().trim.split("\n")
    val opponentsPrefix: String = "Bob: "
    val opponentsHistory: List[(Position, ShotOutcome.Value)] = history.toList.filter(_.startsWith(opponentsPrefix)).map(s => {
      val split: Array[String] = s.substring(opponentsPrefix.length).split(" => ")
      val shotPart: String = split(0)
      val coordinates: Array[String] = shotPart.substring(1, shotPart.length - 1).split(", ")
      val shot: Position = pos(coordinates(0).toInt, coordinates(1).toInt)
      val outcome: ShotOutcome.Value = split(1) match {
        case "Hit" => ShotOutcome.Hit
        case "Water" => ShotOutcome.Water
        case "Sunk" => ShotOutcome.Sunk
        case _ => throw new IllegalArgumentException
      }
      (shot, outcome)
    }).toList

    val computerShot = shooter.shoot(Positions.createGrid(10) -- opponentsHistory.map(_._1), opponentsHistory).get

    Http(go("/game/1/shot").POST << "Computer," + computerShot.column + "," + computerShot.row OK as.String)().trim should be("Hit,Bob")
  }

  private def go(path: String): RequestBuilder = {
    url("http://localhost:" + apiServer.getPort + path)
  }

  override def beforeEach() {
    apiServer = new ApiServer(new Board(10, new Fleet(
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