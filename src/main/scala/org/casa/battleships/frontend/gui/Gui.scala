package org.casa.battleships.frontend.gui

import scala.swing._
import collection.immutable.IndexedSeq
import event.ButtonClicked
import org.casa.battleships.frontend.GameView
import org.casa.battleships.{ComputerPlayer, ShotOutcome, Position}
import org.casa.battleships.Position._
import org.casa.battleships.strategy.shooting.{Shooters, Shooter, OneOneShooter}
import org.casa.battleships.strategy.positionchoice.RandomPositionChooser
import collection.mutable
import collection.mutable.ListBuffer


object Gui extends SimpleSwingApplication {
  var userGameView: GameView = _
  var computerGameView: GameView = _
  var computerPlayer: ComputerPlayer = _

  override def main(args: Array[String]) {
    def retrieveArgument(index: Int): Option[String] = {
      if (args.length > index && !args(index).isEmpty) Some(args(index)) else None
    }

    val apiServerUrl = retrieveArgument(1) getOrElse "http://localhost:8080"
    val views = GameView.createGame(new org.casa.battleships.frontend.Transport(apiServerUrl))
    userGameView = views._1
    computerGameView = views._2

    val computerShooter: Shooter = retrieveArgument(2) match {
      case Some("deterministicShooter") => new OneOneShooter
      case _ => (Shooters.bestShooter(new RandomPositionChooser))
    }
    computerPlayer = new ComputerPlayer(computerShooter, 10)

    super.main(args)
  }

  def top = new MainFrame {
    title = "Battleships: Computer vs You"

    val userFleet = userGameView.myFleet()

    val opponentSquareButtons: IndexedSeq[Button] = for (row <- 1 to 10; column <- 1 to 10) yield new Button(" ") {
      name = opponentSquareButtonName(column, row)
      //        minimumSize = new Dimension(100, 100)
    }


    val opponentBoard = new GridPanel(10, 10) {
      contents ++= opponentSquareButtons
    }


    val userSquareButtons: mutable.LinkedHashMap[Position, Button] = mutable.LinkedHashMap((for (row <- 1 to 10; column <- 1 to 10) yield pos(column, row) -> new Button(userFleet(pos(column, row))) {
      name = userSquareButtonName(column, row)
    }): _*)


    val userBoard = new GridPanel(10, 10) {
      contents ++= userSquareButtons.values
    }

    val logPanel = new LogPanel

    contents = new BoxPanel(Orientation.Vertical) {
      contents += new BoxPanel(Orientation.Horizontal) {
        contents += new BoxPanel(Orientation.Vertical) {
          contents += new Label {
            text = "Computer"
          }
          contents += opponentBoard
          border = Swing.EtchedBorder(Swing.Lowered)
        }

        contents += new BoxPanel(Orientation.NoOrientation) {
          border = Swing.EmptyBorder(0, 10, 0, 0)
        }

        contents += new BoxPanel(Orientation.Vertical) {
          contents += new Label {
            text = "You"
          }
          contents += userBoard
          border = Swing.EtchedBorder(Swing.Lowered)
        }

        border = Swing.EmptyBorder(5, 10, 5, 10)
      }

      contents += logPanel
    }

    reactions += {
      case ButtonClicked(button) if button.name.startsWith("opponent_position_") => {
        val bits = button.name.split("_")
        val humanOnComputerShot = pos(bits(2).toInt, bits(3).toInt)
        val humanOnComputerShotOutcome = userGameView.shootOpponent(bits(2).toInt, bits(3).toInt)
        button.text = outcomeToSquareText(humanOnComputerShotOutcome)

        val computerOnHumanShot: Position = computerPlayer.play(computerGameView.historyOfShotsOnOpponent())
        val computerOnHumanShotOutcome = computerGameView.shootOpponent(computerOnHumanShot.column, computerOnHumanShot.row)

        userSquareButtons(computerOnHumanShot).text = outcomeToSquareText(computerOnHumanShotOutcome)

        logPanel.addLines(
          "User: " + humanOnComputerShot + " => " + humanOnComputerShotOutcome,
          "Computer: " + computerOnHumanShot + " => " + computerOnHumanShotOutcome)
        for (outcome <- gameOutcome) logPanel.addLines(outcome)
      }
    }

    listenTo(opponentSquareButtons: _*)
  }

  def opponentSquareButtonName(column: Int, row: Int): String = "opponent_position_" + column + "_" + row

  def userSquareButtonName(column: Int, row: Int): String = "user_position_" + column + "_" + row

  private def outcomeToSquareText(outcome: ShotOutcome.Value): String = {
    if (ShotOutcome.Water == outcome) "·" else "*"
  }

  private def gameOutcome: Option[String] = {
    computerGameView.winner().map(winner => if ("Computer" == winner) "I win!" else "You win!")
  }

  class LogPanel extends ScrollPane {
    var logLines: ListBuffer[String] = ListBuffer[String]()

    val listView = new ListView[String] {
      name = "log"
    }
    contents = listView

    preferredSize = new Dimension(preferredSize.width, 40)

    def addLines(lines: String*) {
      for (line <- lines) {
        logLines += "" + (logLines.size+1) + ". " + line
      }

      listView.listData = logLines
      listView.revalidate()
      verticalScrollBar.value = verticalScrollBar.maximum
    }
  }
}