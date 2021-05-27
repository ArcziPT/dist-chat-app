//name := """chat-app"""
//organization := "com.arczipt"

//version := "1.0-SNAPSHOT"

lazy val chatService = (project in file("chat-service/"))
    .enablePlugins(PlayScala)
    .settings(
        name := "chat-service",
        libraryDependencies ++= commonDependencies ++ chatDependencies
    )

lazy val dbService = (project in file("db-service/"))
    .enablePlugins(JavaAppPackaging)
    .settings(
        name := "db-service",
        libraryDependencies ++= commonDependencies
    )

scalaVersion := "2.13.6"

lazy val commonDependencies = Seq(
    "com.typesafe.akka" %% "akka-actor" % "2.6.14",
    "com.typesafe.akka" %% "akka-http-core" % "10.2.4",
    "com.typesafe.akka" %% "akka-cluster-typed" % "2.6.14",
    "com.typesafe.akka" %% "akka-serialization-jackson" % "2.6.14"
)

lazy val chatDependencies = Seq(
    guice,
    "com.typesafe.play" %% "play" % "2.8.8",
)

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.arczipt.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.arczipt.binders._"