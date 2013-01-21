package org.casa.battleships

import ascii.AsciiDashboard
import fleet.Fleet
import fleet.Ship._
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.{ServletHolder, ServletContextHandler}
import javax.servlet.http.{HttpServletResponse, HttpServletRequest, HttpServlet}
import java.util.concurrent.atomic.AtomicInteger
import org.casa.battleships.Position._
import java.io.StringWriter
import org.apache.commons.io.IOUtils
import scala.Predef._

class ApiServer(port: Int, board: => Board) {
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

          val response = new AsciiDashboard((dashboard.playersToBoards - player).head, player -> visibleBoard).toAscii

          resp.getWriter.println(response)
        } else {
          // /game/:game_id/history
          dashboard.history.reverse.foreach {
            turn: (String, Position, ShotOutcome.Value) =>
              resp.getWriter.println(turn._1 + ": " + turn._2 + " => " + turn._3)
          }

        }
      }
    }), "/game/*")
    server.setHandler(servletContextHandler)
    server.start()
    this
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
    val server: ApiServer = new ApiServer(8080, new Board(10, new Fleet(
      immaculateShip(pos(1, 1), pos(5, 1)),
      immaculateShip(pos(1, 2), pos(4, 2)),
      immaculateShip(pos(1, 3), pos(3, 3)),
      immaculateShip(pos(1, 4), pos(3, 4)),
      immaculateShip(pos(1, 5), pos(2, 5))
    ))).start()
    server.join()
  }
}