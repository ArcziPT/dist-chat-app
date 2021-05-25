name := """chat-app"""
organization := "com.arczipt"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.6"

libraryDependencies += guice
libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.6.14"
libraryDependencies += "com.typesafe.akka" %% "akka-http-core" % "10.2.4"
libraryDependencies += "com.typesafe.play" %% "play" % "2.8.8"

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.arczipt.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.arczipt.binders._"
