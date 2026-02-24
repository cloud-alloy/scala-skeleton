import org.scalajs.linker.interface.ModuleSplitStyle

val scalaV = "3.3.1"
val zioV = "2.1.9"
val zioHttpV = "3.0.1"
val laminarV = "17.0.0"

ThisBuild / scalaVersion := scalaV

lazy val versions = new {
  val awsCdk = "2.113.0"
  val awsLambdaCore = "1.2.3"
  val awsLambdaEvents = "3.11.4"
}

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

// Backend - ZIO HTTP server  
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

// Infrastructure - AWS CDK for deployment
lazy val infrastructure = project
  .in(file("infrastructure"))
  .settings(commonSettings)
  .settings(
    name := "Infrastructure",
    libraryDependencies ++= Seq(
      "software.amazon.awscdk" % "aws-cdk-lib" % versions.awsCdk,
      "software.constructs" % "constructs" % "10.3.0"
    ),
    (Compile / compile) := (Compile / compile)
      .dependsOn(
        backend / assembly
      ) // ensure backend jar is built before infrastructure
      .value
  )
  .enablePlugins(BuildInfoPlugin)
  .settings(
    buildInfoKeys ++= List[BuildInfoKey](
      BuildInfoKey("backendJarName", "backend.jar"),
      BuildInfoKey("backendDir", "backend"),
      BuildInfoKey("backendHandler", "backend.Main")
    ),
    buildInfoPackage := "infrastructure"
  )

// Root project
lazy val root = project
  .in(file("."))
  .aggregate(sharedJS, sharedJVM, frontend, backend)
  .settings(
    name := "scala-fullstack-app"
  )
