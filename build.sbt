name := "battleships"

version := "1.0"

scalaVersion := "2.9.1"

libraryDependencies ++= Seq(
  "junit" % "junit" % "4.10" % "test->default",
  "org.scalatest" %% "scalatest" % "1.6.1" % "test",
  "org.mockito" % "mockito-core" % "1.9.0" % "test",
  "com.typesafe.akka" % "akka-actor" % "2.0-M3"
)