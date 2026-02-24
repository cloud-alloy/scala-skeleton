import org.scalajs.linker.interface.ModuleSplitStyle

val scalaV = "3.3.1"
val zioV = "2.1.9"
val zioHttpV = "3.0.1"
val laminarV = "17.0.0"

ThisBuild / scalaVersion := scalaV

lazy val commonSettings = List(
  scalafmtOnCompile := true
)

// Shared code between frontend and backend
lazy val shared = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .in(file("shared"))
  .settings(
    libraryDependencies ++= Seq(
      "dev.zio" %%% "zio" % zioV
    )
  )

lazy val sharedJS = shared.js
lazy val sharedJVM = shared.jvm

// Frontend - Scala.js + Laminar
lazy val frontend = project
  .in(file("frontend"))
  .enablePlugins(ScalaJSPlugin)
  .dependsOn(sharedJS)
  .settings(
    scalaJSUseMainModuleInitializer := true,
    scalaJSLinkerConfig ~= {
      _.withModuleKind(ModuleKind.ESModule)
        .withModuleSplitStyle(ModuleSplitStyle.SmallModulesFor(List("frontend")))
    },
    libraryDependencies ++= Seq(
      "com.raquo" %%% "laminar" % laminarV,
      "dev.zio" %%% "zio" % zioV,
      "org.scala-js" %%% "scalajs-dom" % "2.8.0"
    )
  )

// Backend - ZIO HTTP server (serves API + static frontend assets)
lazy val backend = project
  .in(file("backend"))
  .dependsOn(sharedJVM)
  .settings(commonSettings)
  .settings(
    libraryDependencies ++= Seq(
      "dev.zio" %% "zio" % zioV,
      "dev.zio" %% "zio-http" % zioHttpV
    ),
    assembly / assemblyJarName := "backend.jar",
    assembly / assemblyMergeStrategy := {
      case PathList("META-INF", xs @ _*) => MergeStrategy.discard
      case "application.conf" => MergeStrategy.concat
      case x => MergeStrategy.first
    }
  )
  .enablePlugins(AssemblyPlugin)

// Root project
lazy val root = project
  .in(file("."))
  .aggregate(sharedJS, sharedJVM, frontend, backend)
  .settings(
    name := "scala-fullstack-app"
  )
