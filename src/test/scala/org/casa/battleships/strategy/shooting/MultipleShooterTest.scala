package org.casa.battleships.strategy.shooting

import org.scalatest.FunSuite
import org.mockito.Mockito._
import org.mockito.Matchers._
import org.mockito.InOrder
import org.casa.battleships.Position.pos
import org.casa.battleships.{ShotOutcome, Position}
import org.casa.battleships.ShotOutcome.Water
import org.junit.Assert.assertThat
import org.hamcrest.CoreMatchers.is
import org.hamcrest.{Matcher, CoreMatchers}

class MultipleShooterTest extends FunSuite {

  val shootable: Set[Position] = Set[Position](pos(1, 1))
  val history: List[(Position, ShotOutcome.Value)] = (pos(2, 1), Water) :: (pos(1, 2), Water) :: (pos(2, 2), Water) :: Nil

  def anyShootable: Set[Position] = anyObject()
  def anyHistory: List[(Position, ShotOutcome.Value)] = anyObject()

  def failingShooter: Shooter = {
    val shooter = mock(classOf[Shooter])
    when(shooter.shoot(anyShootable, anyHistory)).thenReturn(None)
    shooter
  }

  def somePosition: Some[Position] = {
    Some(pos(1, 1))
  }

  def anotherPosition: Some[Position] = {
    Some(pos(1, 2))
  }

  def shooterThatShootsAt(position: Some[Position]): Shooter = {
    val shooter = mock(classOf[Shooter])
    when(shooter.shoot(anyShootable, anyHistory)).thenReturn(position)
    shooter
  }

  def isNone: Matcher[Option[Position]] = {
    CoreMatchers.is[Option[Position]](None)
  }
  
  test("fail when all shooters fail"){
    val multipleShooter: MultipleShooter = new MultipleShooter(failingShooter, failingShooter, failingShooter)
    assertThat(multipleShooter.shoot(shootable, history), isNone)
  }
  
  test("call all shooters until one suceeds"){
    val shooter1 = failingShooter
    val shooter2 = failingShooter
    val shooter3 = shooterThatShootsAt(somePosition)
    val shooter4 = mock(classOf[Shooter])

    new MultipleShooter(shooter1, shooter2, shooter3, shooter4).shoot(shootable, history)

    val sequentially: InOrder = inOrder(shooter1, shooter2, shooter3)
    sequentially.verify(shooter1).shoot(anyShootable, anyHistory)
    sequentially.verify(shooter2).shoot(anyShootable, anyHistory)
    sequentially.verify(shooter3).shoot(anyShootable, anyHistory)
    verify(shooter4, never()).shoot(anyShootable, anyHistory)
  }
  
  test("returns what the first succeeding shooter returns"){
    val multipleShooter = new MultipleShooter(failingShooter, failingShooter, shooterThatShootsAt(somePosition), shooterThatShootsAt(anotherPosition))
    assertThat(multipleShooter.shoot(shootable, history), CoreMatchers.is[Option[Position]](somePosition))
  }
}