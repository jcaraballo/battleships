package acceptance

import org.scalatest.{BeforeAndAfterEach, FunSuite}
import org.casa.battleships.{ComputerPlayer, Board, ApiServer}
import org.casa.battleships.strategy.FleetComposer
import org.casa.battleships.strategy.positionchoice.UpmostAndThenLeftmostPositionChooser
import org.casa.battleships.fleet.Bag
import org.casa.battleships.strategy.shooting.OneOneShooter
import org.casa.battleships.frontend.cli.Cli
import org.casa.battleships.frontend.Transport
import org.scalatest.matchers.ShouldMatchers


class CliAcceptanceTest extends FunSuite with BeforeAndAfterEach with ShouldMatchers {
  var server: ApiServer = _
  var cli: Cli = _

  override def beforeEach() {
    server = new ApiServer(new Board(10, new FleetComposer(new UpmostAndThenLeftmostPositionChooser).create(10, Bag(5, 4, 3, 3, 2).toList).get))
    server.start()

    val computerPlayer = new ComputerPlayer(new OneOneShooter, 10)

    cli = new Cli(new Transport("http://localhost:" + server.getPort), computerPlayer)
  }

  override def afterEach() {
    server.stop()
  }

  test("plays") {
    cli.restart shouldBe
      """
        |==========
        |New Game
        |==========
        |  Computer                 You                  .
        |  1 2 3 4 5 6 7 8 9 0      1 2 3 4 5 6 7 8 9 0  .
        |  ~~~~~~~~~~~~~~~~~~~      ~~~~~~~~~~~~~~~~~~~  .
        |1{                   }1  1{< - - - > < - - > ^}1.
        |2{                   }2  2{< - > < >         |}2.
        |3{                   }3  3{                  v}3.
        |4{                   }4  4{                   }4.
        |5{                   }5  5{                   }5.
        |6{                   }6  6{                   }6.
        |7{                   }7  7{                   }7.
        |8{                   }8  8{                   }8.
        |9{                   }9  9{                   }9.
        |0{                   }0  0{                   }0.
        |  ~~~~~~~~~~~~~~~~~~~      ~~~~~~~~~~~~~~~~~~~  .
        |  1 2 3 4 5 6 7 8 9 0      1 2 3 4 5 6 7 8 9 0  .
        |
        |Enter your move:
        |.""".stripMargin.filter(_ != '.')

    cli < "shoot 1 1" shouldBe
      """
        |User: (1, 1) => Hit
        |Computer: (1, 1) => Hit
        |
        |  Computer                 You                  .
        |  1 2 3 4 5 6 7 8 9 0      1 2 3 4 5 6 7 8 9 0  .
        |  ~~~~~~~~~~~~~~~~~~~      ~~~~~~~~~~~~~~~~~~~  .
        |1{*                  }1  1{* - - - > < - - > ^}1.
        |2{                   }2  2{< - > < >         |}2.
        |3{                   }3  3{                  v}3.
        |4{                   }4  4{                   }4.
        |5{                   }5  5{                   }5.
        |6{                   }6  6{                   }6.
        |7{                   }7  7{                   }7.
        |8{                   }8  8{                   }8.
        |9{                   }9  9{                   }9.
        |0{                   }0  0{                   }0.
        |  ~~~~~~~~~~~~~~~~~~~      ~~~~~~~~~~~~~~~~~~~  .
        |  1 2 3 4 5 6 7 8 9 0      1 2 3 4 5 6 7 8 9 0  .
        |
        |Enter your move:
        |.""".stripMargin.filter(_ != '.')

    cli < "2 1" shouldBe
      """
        |User: (2, 1) => Hit
        |Computer: (1, 1) => Hit
        |
        |  Computer                 You                  .
        |  1 2 3 4 5 6 7 8 9 0      1 2 3 4 5 6 7 8 9 0  .
        |  ~~~~~~~~~~~~~~~~~~~      ~~~~~~~~~~~~~~~~~~~  .
        |1{* *                }1  1{* - - - > < - - > ^}1.
        |2{                   }2  2{< - > < >         |}2.
        |3{                   }3  3{                  v}3.
        |4{                   }4  4{                   }4.
        |5{                   }5  5{                   }5.
        |6{                   }6  6{                   }6.
        |7{                   }7  7{                   }7.
        |8{                   }8  8{                   }8.
        |9{                   }9  9{                   }9.
        |0{                   }0  0{                   }0.
        |  ~~~~~~~~~~~~~~~~~~~      ~~~~~~~~~~~~~~~~~~~  .
        |  1 2 3 4 5 6 7 8 9 0      1 2 3 4 5 6 7 8 9 0  .
        |
        |Enter your move:
        |.""".stripMargin.filter(_ != '.')

    cli < "3 1"
    cli < "4 1"
    cli < "5 1"

    cli < "6 1"
    cli < "7 1"
    cli < "8 1"
    cli < "9 1"

    cli < "10 1"
    cli < "10 2"
    cli < "10 3"

    cli < "1 2"
    cli < "2 2"
    cli < "3 2"

    cli < "4 2"
    cli < "5 2" shouldBe
      """
        |User: (5, 2) => Sunk
        |Computer: (1, 1) => Hit
        |
        |  Computer                 You                  .
        |  1 2 3 4 5 6 7 8 9 0      1 2 3 4 5 6 7 8 9 0  .
        |  ~~~~~~~~~~~~~~~~~~~      ~~~~~~~~~~~~~~~~~~~  .
        |1{* * * * * * * * * *}1  1{* - - - > < - - > ^}1.
        |2{* * * * *         *}2  2{< - > < >         |}2.
        |3{                  *}3  3{                  v}3.
        |4{                   }4  4{                   }4.
        |5{                   }5  5{                   }5.
        |6{                   }6  6{                   }6.
        |7{                   }7  7{                   }7.
        |8{                   }8  8{                   }8.
        |9{                   }9  9{                   }9.
        |0{                   }0  0{                   }0.
        |  ~~~~~~~~~~~~~~~~~~~      ~~~~~~~~~~~~~~~~~~~  .
        |  1 2 3 4 5 6 7 8 9 0      1 2 3 4 5 6 7 8 9 0  .
        |
        |You win!
        |
        |==========
        |New Game
        |==========
        |  Computer                 You                  .
        |  1 2 3 4 5 6 7 8 9 0      1 2 3 4 5 6 7 8 9 0  .
        |  ~~~~~~~~~~~~~~~~~~~      ~~~~~~~~~~~~~~~~~~~  .
        |1{                   }1  1{< - - - > < - - > ^}1.
        |2{                   }2  2{< - > < >         |}2.
        |3{                   }3  3{                  v}3.
        |4{                   }4  4{                   }4.
        |5{                   }5  5{                   }5.
        |6{                   }6  6{                   }6.
        |7{                   }7  7{                   }7.
        |8{                   }8  8{                   }8.
        |9{                   }9  9{                   }9.
        |0{                   }0  0{                   }0.
        |  ~~~~~~~~~~~~~~~~~~~      ~~~~~~~~~~~~~~~~~~~  .
        |  1 2 3 4 5 6 7 8 9 0      1 2 3 4 5 6 7 8 9 0  .
        |
        |Enter your move:
        |.""".stripMargin.filter(_ != '.')
  }

  test("plays again"){
    cli.restart
    cli < "1 1"
    cli < "2 1"
    cli < "3 1"
    cli < "4 1"
    cli < "5 1"

    cli < "6 1"
    cli < "7 1"
    cli < "8 1"
    cli < "9 1"

    cli < "10 1"
    cli < "10 2"
    cli < "10 3"

    cli < "1 2"
    cli < "2 2"
    cli < "3 2"

    cli < "4 2"
    cli < "5 2" should endWith(
      """
        |You win!
        |
        |==========
        |New Game
        |==========
        |  Computer                 You                  .
        |  1 2 3 4 5 6 7 8 9 0      1 2 3 4 5 6 7 8 9 0  .
        |  ~~~~~~~~~~~~~~~~~~~      ~~~~~~~~~~~~~~~~~~~  .
        |1{                   }1  1{< - - - > < - - > ^}1.
        |2{                   }2  2{< - > < >         |}2.
        |3{                   }3  3{                  v}3.
        |4{                   }4  4{                   }4.
        |5{                   }5  5{                   }5.
        |6{                   }6  6{                   }6.
        |7{                   }7  7{                   }7.
        |8{                   }8  8{                   }8.
        |9{                   }9  9{                   }9.
        |0{                   }0  0{                   }0.
        |  ~~~~~~~~~~~~~~~~~~~      ~~~~~~~~~~~~~~~~~~~  .
        |  1 2 3 4 5 6 7 8 9 0      1 2 3 4 5 6 7 8 9 0  .
        |
        |Enter your move:
        |.""".stripMargin.filter(_ != '.'))

    cli < "2 1" shouldBe
      """
        |User: (2, 1) => Hit
        |Computer: (1, 1) => Hit
        |
        |  Computer                 You                  .
        |  1 2 3 4 5 6 7 8 9 0      1 2 3 4 5 6 7 8 9 0  .
        |  ~~~~~~~~~~~~~~~~~~~      ~~~~~~~~~~~~~~~~~~~  .
        |1{  *                }1  1{* - - - > < - - > ^}1.
        |2{                   }2  2{< - > < >         |}2.
        |3{                   }3  3{                  v}3.
        |4{                   }4  4{                   }4.
        |5{                   }5  5{                   }5.
        |6{                   }6  6{                   }6.
        |7{                   }7  7{                   }7.
        |8{                   }8  8{                   }8.
        |9{                   }9  9{                   }9.
        |0{                   }0  0{                   }0.
        |  ~~~~~~~~~~~~~~~~~~~      ~~~~~~~~~~~~~~~~~~~  .
        |  1 2 3 4 5 6 7 8 9 0      1 2 3 4 5 6 7 8 9 0  .
        |
        |Enter your move:
        |.""".stripMargin.filter(_ != '.')

  }
}