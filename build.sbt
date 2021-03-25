
lazy val akkaHttpVersion = "10.2.4"
lazy val akkaVersion    = "2.6.13"
val akkaManagementVersion = "1.0.10"

lazy val root = (project in file("."))
  .settings(
    inThisBuild(List(
      organization    := "com.example",
      scalaVersion    := "2.13.4"
    )),
    name := "poc-akka-consul",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-http"                % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-http-spray-json"     % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-actor-typed"         % akkaVersion,
      "com.typesafe.akka" %% "akka-stream"              % akkaVersion,
      "com.typesafe.akka" %% "akka-cluster-typed"       % akkaVersion,
      "com.lightbend.akka.management" %% "akka-management-cluster-bootstrap" % akkaManagementVersion,
      "com.lightbend.akka.management" %% "akka-management-cluster-http" % akkaManagementVersion,
      "com.lightbend.akka.discovery" %% "akka-discovery-consul" % akkaManagementVersion,
      "com.typesafe.akka" %% "akka-cluster-sharding" % akkaVersion,
      "com.typesafe.akka" %% "akka-discovery" % akkaVersion,
      "ch.qos.logback"    % "logback-classic"           % "1.2.3",

      "com.typesafe.akka" %% "akka-http-testkit"        % akkaHttpVersion % Test,
      "com.typesafe.akka" %% "akka-actor-testkit-typed" % akkaVersion     % Test,
      "org.scalatest"     %% "scalatest"                % "3.1.4"         % Test,
      "com.pszymczyk.consul" % "embedded-consul" % "1.0.2",
      "com.orbitz.consul" % "consul-client" % "1.1.2" % "test",
      "com.google.guava" % "guava" % "27.0.1-jre" % "test",
    ),
    mainClass in (Compile, run) := Some("com.example.QuickstartApp")
  )
