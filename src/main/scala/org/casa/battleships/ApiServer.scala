package org.casa.battleships

import ascii.AsciiDashboard
import fleet.Bag
import org.eclipse.jetty.server.{ServerConnector, Server}
import org.eclipse.jetty.servlet.{ServletHolder, ServletContextHandler}
import javax.servlet.http.{HttpServletResponse, HttpServletRequest, HttpServlet}
import java.util.concurrent.atomic.AtomicInteger
import org.casa.battleships.Position._
import java.io.StringWriter
import org.apache.commons.io.IOUtils
import scala.Predef._
import strategy.FleetComposer
import strategy.positionchoice.RandomPositionChooser
import scala.Some

class ApiServer(port: Int, board: => Board) {
  def this(board: => Board) = this(0, board)
  def this() = this(8080, new Board(10, new FleetComposer(new RandomPositionChooser()).create(10, Bag(5, 4, 3, 3, 2).toList).get))

  val server = new Server(port)

  var games: Map[String, Dashboard] = Map()
  val gameIdSource: AtomicInteger = new AtomicInteger()

  def start(): ApiServer = {
    val servletContextHandler = new ServletContextHandler()
    servletContextHandler.setContextPath("/")
    servletContextHandler.addServlet(new ServletHolder(new HttpServlet {
      override def doPost(req: HttpServletRequest, resp: HttpServletResponse) {
        val parts = req.getRequestURI.substring(1).split('/')
        if (parts.size == 1) {
          val body = retrieveBody(req)

          val bodyParts = body.split(',')
          val playerId1 = bodyParts(0)
          val playerId2 = bodyParts(1)

          val newGameId = gameIdSource.incrementAndGet().toString
          games += newGameId -> new Dashboard((playerId1, board), (playerId2, board))
          resp.getWriter.println(newGameId + "," + playerId1)
        } else {
          // /game/:id/shot
          val game = parts(1)

          val body = retrieveBody(req)
          val bodyParts = body.split(',')
          val shooter = bodyParts(0)
          val x = bodyParts(1).toInt
          val y = bodyParts(2).toInt

          val (outcome, nextPlayer) = games.get(game).get.shoot(shooter, pos(x, y))

          val outcome1: String = "" + outcome
          val response = "" + outcome1 + "," + nextPlayer
          resp.getWriter.println(response)
        }
      }

      override def doGet(req: HttpServletRequest, resp: HttpServletResponse) {
        val parts = req.getRequestURI.substring(1).split('/')
        val game = parts(1)
        val dashboard = games.get(game).get

        if ("dashboard" == parts(2)) {
          // /game/:game_id/dashboard/:player_id

          val player = parts(3)

          val visibleBoard: Board = dashboard.playersToBoards.get(player).get
          val thisPlayerWithVisibleBoard = player -> visibleBoard

          val otherPlayerWithHiddenBoard = (dashboard.playersToBoards - player).head

          val response = new AsciiDashboard(otherPlayerWithHiddenBoard, thisPlayerWithVisibleBoard).toAscii
          resp.getWriter.println(response)
        } else if ("history" == parts(2)) {
          // /game/:game_id/history
          dashboard.history.reverse.foreach {
            turn: (String, Position, ShotOutcome.Value) =>
              resp.getWriter.println(turn._1 + ": " + turn._2 + " => " + turn._3)
          }
        } else if ("winner" == parts(2)) {
          dashboard.playersToBoards.find(_._2.areAllShipsSunk) match {
            case Some((player, _)) => resp.getWriter.println(dashboard.opponent(player))
            case None => resp.setStatus(404)
          }
        } else
          resp.setStatus(404)
      }
    }), "/game/*")
    server.setHandler(servletContextHandler)
    server.start()
    println("Server started on port " + getPort)
    this
  }

  def getPort: Int = {
    server.getConnectors.head.asInstanceOf[ServerConnector].getLocalPort
  }

  def stop() {
    server.stop()
  }

  def join() {
    server.join()
  }

  private def retrieveBody(req: HttpServletRequest): String = {
    val writer = new StringWriter()
    IOUtils.copy(req.getInputStream, writer, "UTF-8")
    writer.toString
  }
}

object ApiServer {
  def main(args: Array[String]) {
    val server = new ApiServer
    server.start()

    server.join()
  }
}