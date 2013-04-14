name := "battleships"

version := "1.0"

scalaVersion := "2.10.1"

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies ++= Seq(
  "junit" % "junit" % "4.11" % "test->default",
  "org.scalatest" %% "scalatest" % "2.0.M5b" % "test",
  "org.mockito" % "mockito-core" % "1.9.5" % "test",
  "com.typesafe.akka" %% "akka-actor" % "2.1.0",
  "com.typesafe.akka" %% "akka-testkit" % "2.1.0" % "test",
  "net.databinder.dispatch" %% "dispatch-core" % "0.9.5",
  "org.eclipse.jetty" % "jetty-server" % "9.0.0.M4",
  "org.eclipse.jetty" % "jetty-servlet" % "9.0.0.M4",
  "commons-io" % "commons-io" % "2.4",
  "org.scala-lang" % "scala-swing" % "2.10.1",
  "com.googlecode.windowlicker" % "windowlicker-core" % "r268",
  "com.googlecode.windowlicker" % "windowlicker-swing" % "r268"
)
