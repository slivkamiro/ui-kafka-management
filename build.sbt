import scala.sys.process._
import scala.language.postfixOps

import sbtwelcome._

Global / onChangedBuildSource := ReloadOnSourceChanges

ThisBuild / scalafixDependencies += "com.github.liancheng" %% "organize-imports" % "0.6.0"

lazy val tyriancourse =
  (project in file("."))
    .enablePlugins(ScalaJSPlugin)
    .settings( // Normal settings
      name         := "tyrian-course",
      version      := "0.0.1",
      scalaVersion := "3.2.2",
      organization := "me.slivkamiro",
      libraryDependencies ++= Seq(
        "io.indigoengine" %%% "tyrian-io" % "0.6.2",
        "io.circe" %%% "circe-parser" % "0.14.1",
        "org.scalameta"   %%% "munit"     % "0.7.29" % Test
      ),
      testFrameworks += new TestFramework("munit.Framework"),
      scalaJSLinkerConfig ~= { _.withModuleKind(ModuleKind.CommonJSModule) },
      scalafixOnCompile := true,
      semanticdbEnabled := true,
      semanticdbVersion := scalafixSemanticdb.revision,
      autoAPIMappings   := true
    )
    .settings( // Launch VSCode when you type `code` in the sbt terminal
      code := {
        val command = Seq("code", ".")
        val run = sys.props("os.name").toLowerCase match {
          case x if x contains "windows" => Seq("cmd", "/C") ++ command
          case _                         => command
        }
        run.!
      }
    )
    .settings( // Welcome message
      logo := List(
        "",
        "Tyrian Course (v" + version.value + ")",
        "",
        "> Please Note: By default tyrianapp.js expects you to run fastLinkJS.",
        ">              To use fullOptJS, edit tyrianapp.js replacing '-fastopt'",
        ">              with '-opt'.",
        ""
      ).mkString("\n"),
      usefulTasks := Seq(
        UsefulTask("fastLinkJS", "Rebuild the JS (use during development)").noAlias,
        UsefulTask("fullLinkJS", "Rebuild the JS and optimise (use in production)").noAlias,
        UsefulTask("code", "Launch VSCode").noAlias
      ),
      logoColor        := scala.Console.MAGENTA,
      aliasColor       := scala.Console.BLUE,
      commandColor     := scala.Console.CYAN,
      descriptionColor := scala.Console.WHITE
    )

lazy val code =
  taskKey[Unit]("Launch VSCode in the current directory")


  lazy val backend = (project in file("backend"))
      .settings(
        name := "backend",
        version := "0.0.1",
        scalaVersion := "3.2.2",
        organization := "me.slivkamiro",
        libraryDependencies ++= Seq(
          "com.github.fd4s" %% "fs2-kafka" % "3.0.1",
          "org.http4s" %% "http4s-ember-server" % "0.23.18",
          "com.softwaremill.sttp.tapir" %% "tapir-http4s-server" % "1.3.0",
          "com.softwaremill.sttp.tapir" %% "tapir-json-circe" % "1.3.0"
        ),
        testFrameworks += new TestFramework("munit.Framework"),
        semanticdbEnabled := true,
        semanticdbVersion := scalafixSemanticdb.revision,
      )
