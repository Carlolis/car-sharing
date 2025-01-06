val scala3Version = "3.3.1"

lazy val root = project
  .in(file("."))
  .settings(
    name := "back",
    version := "0.1.1",
    scalaVersion := scala3Version,
    Revolver.enableDebugging(port = 5005, suspend = false),
    libraryDependencies ++= Seq(
      "org.scalameta" %% "munit" % "1.0.3" % Test,
      "dev.zio" %% "zio" % "2.1.14",
      "dev.zio" %% "zio-http" % "3.0.1",
      "com.softwaremill.sttp.tapir" %% "tapir-core" % "1.11.10",
      "com.softwaremill.sttp.tapir" %% "tapir-zio" % "1.11.10",
      "com.softwaremill.sttp.tapir" %% "tapir-zio-http-server" % "1.11.10",
      "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-bundle" % "1.11.10",
      "com.softwaremill.sttp.tapir" %% "tapir-json-zio" % "1.11.10",
      "dev.zio" %% "zio-json" % "0.7.3",
      "com.auth0" % "java-jwt" % "4.4.0",
      "io.scalaland" %% "chimney" % "1.6.0",
      "io.scalaland" %% "chimney-java-collections" % "1.6.0",
      "com.edgedb" % "driver" % "0.3.0",
      "dev.zio" %% "zio-test" % "2.1.14" % Test,
      "dev.zio" %% "zio-test-sbt" % "2.1.14" % Test,
      "dev.zio" %% "zio-test-magnolia" % "2.1.14" % Test,
      "com.github.sbt" % "junit-interface" % "0.13.3" % Test,
      "dev.zio" %% "zio-test-junit" % "2.1.14" % Test
    ),
    Compile / mainClass := Some("Main")
  )
