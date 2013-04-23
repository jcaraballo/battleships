package org.casa.battleships.frontend.cli

import org.casa.battleships.frontend.{UiArguments, Transport}
import org.casa.battleships.ComputerPlayer

import org.casa.battleships.Position.pos
import scala.util.parsing.combinator.JavaTokenParsers


class Cli(transport: Transport, computerPlayer: ComputerPlayer) extends JavaTokenParsers {

  def command: Parser[String] = shot | quit

  def shot: Parser[String] = opt("shoot") ~> wholeNumber ~ wholeNumber ^^ {
    case column ~ row => game.shoot(pos(column.toInt, row.toInt))
  }

  def quit: Parser[String] = "quit" ^^ {
    x => sys.exit(-1)
  }

  val game = new Game(transport, computerPlayer)


  def <(input: String): String = {
    parseAll(command, input).get
  }

  def restart = game.restart

}

object Cli {
  def main(args: Array[String]) {
    val arguments = new UiArguments(args)
    val cli = new Cli(arguments.transport, arguments.computerPlayer)

    var output = cli.restart
    while (true) {
      val line = readLine(output)
      output = cli < line
    }
  }
}