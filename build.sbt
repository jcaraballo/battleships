name := "battleships"

version := "1.0"

scalaVersion := "2.9.1"

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies ++= Seq(
  "junit" % "junit" % "4.10" % "test->default",
  "org.scalatest" %% "scalatest" % "2.0.M5b" % "test",
  "org.mockito" % "mockito-core" % "1.9.5" % "test",
  "com.typesafe.akka" % "akka-actor" % "2.0.1",
  "com.typesafe.akka" % "akka-testkit" % "2.0.1" % "test",
  "ch.qos.logback" % "logback-classic" % "1.0.0",
  "org.clapper" %% "grizzled-slf4j" % "0.6.6"
)
