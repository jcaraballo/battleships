package acceptance

import com.objogate.wl.swing.driver.{JListDriver, JButtonDriver, ComponentDriver, JFrameDriver}
import com.objogate.wl.swing.gesture.GesturePerformer
import com.objogate.wl.swing.AWTEventQueueProber
import org.scalatest.{BeforeAndAfterEach, FunSuite}
import org.casa.battleships.frontend.gui.Gui

import concurrent.ExecutionContext
import ExecutionContext.Implicits.global
import org.hamcrest.{TypeSafeMatcher, Description}
import javax.swing.{JList, JButton, JFrame}
import java.awt.Component
import org.casa.battleships.{Position, Board, ApiServer}
import org.casa.battleships.strategy.FleetComposer
import org.casa.battleships.strategy.positionchoice.UpmostAndThenLeftmostPositionChooser
import org.casa.battleships.fleet.Bag
import org.casa.battleships.frontend.GameView

class GuiAcceptanceTest extends FunSuite with BeforeAndAfterEach {
  var server: ApiServer = _

  var driver: BattleshipsGuiDriver = _
  var app: Unit = _

  override def beforeEach() {
    server = new ApiServer(new Board(10, new FleetComposer(new UpmostAndThenLeftmostPositionChooser).create(10, Bag(5, 4, 3, 3, 2).toList).get))
    server.start()

    scala.concurrent.future {
      app = Gui.main(Array("", "http://localhost:" + server.getPort, "deterministicShooter"))
    }
    driver = new BattleshipsGuiDriver
    driver.hasTitle("Battleships: Computer vs You")
  }

  override def afterEach() {
    driver.dispose()
    server.stop()
  }

  test("plays") {
    driver.dashboardIs(
      """
        |  Computer                 You
        |  1 2 3 4 5 6 7 8 9 0      1 2 3 4 5 6 7 8 9 0
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
        |  1 2 3 4 5 6 7 8 9 0      1 2 3 4 5 6 7 8 9 0  .""")

    driver.onComputerSquare(1, 1).click()

    driver.dashboardIs(
      """
        |  Computer                 You
        |  1 2 3 4 5 6 7 8 9 0      1 2 3 4 5 6 7 8 9 0
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
        |  1 2 3 4 5 6 7 8 9 0      1 2 3 4 5 6 7 8 9 0  .""")
    driver.log.hasLines(
      "1. User: (1, 1) => Hit",
      "2. Computer: (1, 1) => Hit")

    driver.onComputerSquare(2, 1).click()

    driver.dashboardIs(
      """
        |  Computer                 You
        |  1 2 3 4 5 6 7 8 9 0      1 2 3 4 5 6 7 8 9 0
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
        |  1 2 3 4 5 6 7 8 9 0      1 2 3 4 5 6 7 8 9 0  .""")
    driver.log.hasLines(
      "3. User: (2, 1) => Hit",
      "4. Computer: (1, 1) => Hit")

    driver.onComputerSquare(3, 1).click()
    driver.onComputerSquare(4, 1).click()
    driver.onComputerSquare(5, 1).click()

    driver.dashboardIs(
      """
        |  Computer                 You
        |  1 2 3 4 5 6 7 8 9 0      1 2 3 4 5 6 7 8 9 0
        |  ~~~~~~~~~~~~~~~~~~~      ~~~~~~~~~~~~~~~~~~~  .
        |1{* * * * *          }1  1{* - - - > < - - > ^}1.
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
        |  1 2 3 4 5 6 7 8 9 0      1 2 3 4 5 6 7 8 9 0  .""")
    driver.log.hasLines(
      "9. User: (5, 1) => Sunk",
      "10. Computer: (1, 1) => Hit")


    driver.onComputerSquare(6, 1).click()
    driver.onComputerSquare(7, 1).click()
    driver.onComputerSquare(8, 1).click()
    driver.onComputerSquare(9, 1).click()

    driver.onComputerSquare(10, 1).click()
    driver.onComputerSquare(10, 2).click()
    driver.onComputerSquare(10, 3).click()

    driver.onComputerSquare(1, 2).click()
    driver.onComputerSquare(2, 2).click()
    driver.onComputerSquare(3, 2).click()

    driver.onComputerSquare(4, 2).click()
    driver.onComputerSquare(5, 2).click()

    driver.log.hasLines(
      "33. User: (5, 2) => Sunk",
      "34. Computer: (1, 1) => Hit",
      "35. You win!"
    )
  }

  class BattleshipsGuiDriver extends JFrameDriver(new GesturePerformer(), JFrameDriver.topLevelFrame(
    ComponentDriver.showingOnScreen()), new AWTEventQueueProber(1000, 100)) {
    def dashboardIs(dashboard: String) {

      val computerBoardMap = convertToBoardMap(dashboard, _.indexOf("{"), _.indexOf("}"))
      val humanBoardMap = convertToBoardMap(dashboard, _.lastIndexOf("{"), _.lastIndexOf("}"))

      for ((key, value) <- computerBoardMap) onComputerSquare(key.column, key.row).hasText(value)
      for ((key, value) <- humanBoardMap) onHumanSquare(key.column, key.row).hasText(value)
    }

    def onComputerSquare(column: Int, row: Int) = new SquareDriver(this, Gui.opponentSquareButtonName(column, row))

    def onHumanSquare(column: Int, row: Int) = new SquareDriver(this, Gui.userSquareButtonName(column, row))

    def log = new JListDriver(this, classOf[JList[_]], ComponentDriver.named("log")) {
      def hasLines(lines: String*) {
        for (line <- lines) hasItem(org.hamcrest.Matchers.endsWith(line))
      }
    }


    private def convertToBoardMap(source: String, leftBorderIndexOrMinusOne: String => Int, rightBorderIndexOrMinusOne: String => Int): Map[Position, String] = {
      val payload: Array[String] = source.trim.split('\n').map {
        line =>
          val leftBorder = leftBorderIndexOrMinusOne(line)
          val rightBorder = rightBorderIndexOrMinusOne(line)
          if (leftBorder == -1 || rightBorder == -1) None else Some(line.substring(leftBorder + 1, rightBorder))
      }.flatten.map(_.zipWithIndex.filter(_._2 % 2 == 0).map(_._1).mkString)
      GameView.convertBoardPayloadToMap(payload)
    }
  }

  class SquareDriver(parentOrOwner: ComponentDriver[_ <: Component], name: String)
    extends JButtonDriver(parentOrOwner, classOf[JButton], ComponentDriver.named(name)) {
    def hasText(text: String) {
      hasText(org.hamcrest.Matchers.equalTo(text))
    }

  }

  def titled(title: String): TypeSafeMatcher[JFrame] = new TypeSafeMatcher[JFrame] {
    def matchesSafely(jFrame: JFrame): Boolean = {
      title.equals(jFrame.getTitle)
    }

    def describeTo(description: Description) {
      description.appendText("showing on screen")
    }
  }

  private def trimOnTheRight(string: String) = string.split('\n').map(_.reverse.dropWhile(_ == ' ').reverse).toList.mkString("\n") + '\n'
}