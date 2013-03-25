package org.casa.battleships

object ShotOutcome extends Enumeration {
  val Water, Hit, Sunk = Value

  object LookUp {
    def fromString(string: String): ShotOutcome.Value = {
      string match {
        case "Hit" => ShotOutcome.Hit
        case "Water" => ShotOutcome.Water
        case "Sunk" => ShotOutcome.Sunk
        case _ => throw new IllegalArgumentException
      }
    }
  }
}

