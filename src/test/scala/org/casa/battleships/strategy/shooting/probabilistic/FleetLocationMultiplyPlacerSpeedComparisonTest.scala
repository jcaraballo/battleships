package org.casa.battleships.strategy.shooting.probabilistic

import org.scalatest.FunSuite
import FleetLocationMultiplyPlacer.findAllValidLocations
import FleetLocationMultiplyPlacer.findAllValidLocations_slower
import testtools.Stopwatch.time
import org.scalatest.matchers.ShouldMatchers
import grizzled.slf4j.Logger
import collection.immutable.Set
import org.casa.battleships.{Position, Positions}
import util.Random

class FleetLocationMultiplyPlacerSpeedComparisonTest extends FunSuite with ShouldMatchers {
  val logger = Logger(classOf[FleetLocationMultiplyPlacerSpeedComparisonTest])

  def createRandomPosition(max: Int): Position = {
    Position(Random.nextInt(max) + 1, Random.nextInt(max) + 1)
  }

  def createGridWithHoles(size: Int, numberOfHoles: Int): Set[Position] = {
    var holes = Set[Position]()
    while (holes.size < numberOfHoles) {
      holes += createRandomPosition(size)
    }

    Positions.createGrid(size) -- holes
  }

  def isPercentSlower(slower: Long, faster: Long, percent: Double): Boolean = {
    ((1 + percent / 100) * faster) < slower
  }

  def executeInRandomOrder(f: => Unit)(g: => Unit) {
    if (Random.nextBoolean()) {
      f
      g
    } else {
      g
      f
    }
  }

  test("old method is generally slower than new one") {
    val grids = (1 to 20).map(i => createGridWithHoles(6, 5))

    val slowerTimes = scala.collection.mutable.ArrayBuffer.empty[Long]
    val fasterTimes = scala.collection.mutable.ArrayBuffer.empty[Long]

    val shipSizes = 5 :: 4 :: 3 :: 3 :: 2 :: Nil

    for (grid <- grids) {
      executeInRandomOrder {
        slowerTimes += time(findAllValidLocations_slower(shipSizes, grid))._2
      } {
        fasterTimes += time(findAllValidLocations(shipSizes, grid))._2
      }
    }

    logger.info("slower: " + slowerTimes)
    logger.info("faster: " + fasterTimes)

    logger.info("max slow: " + slowerTimes.max)
    logger.info("min slow: " + slowerTimes.min)
    logger.info("avg slow: " + slowerTimes.sum.toDouble / slowerTimes.size)

    logger.info("max fast: " + fasterTimes.max)
    logger.info("min fast: " + fasterTimes.min)
    logger.info("avg fast: " + fasterTimes.sum.toDouble / slowerTimes.size)

    val zipped = slowerTimes zip fasterTimes

    val percentageOfExecutionsWhereSlowerIsAtLeast10PercentSlower = 100.0 *
      zipped.count {
        case (s, f) => isPercentSlower(s, f, 10)
      } / zipped.size

    logger.info("percentageOfExecutionsWhereSlowerIsAtLeast10PercentSlower: " + percentageOfExecutionsWhereSlowerIsAtLeast10PercentSlower)

    val percentageOfExecutionsWhereSlowerIsAtLeast5PercentSlower = 100.0 *
      zipped.count {
        case (s, f) => isPercentSlower(s, f, 5)
      } / zipped.size

    logger.info("percentageOfExecutionsWhereSlowerIsAtLeast5PercentSlower: " + percentageOfExecutionsWhereSlowerIsAtLeast5PercentSlower)

    percentageOfExecutionsWhereSlowerIsAtLeast5PercentSlower should be > (60.0)
    percentageOfExecutionsWhereSlowerIsAtLeast10PercentSlower should be > (40.0)
  }
}