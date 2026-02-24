package infrastructure

import software.amazon.awscdk.{App, Environment, StackProps}

object CdkMain extends scala.App {
  val environment = Environment.builder()
    .account(
      sys.env.getOrElse(
        "CDK_DEFAULT_ACCOUNT",
        throw new IllegalArgumentException("No default account found")
      )
    )
    .region(
      sys.env.getOrElse(
        "CDK_DEFAULT_REGION",
        throw new IllegalArgumentException("No default region found")
      )
    )
    .build()

  val app = new App()
  
  // Create the backend API stack
  val backendStack = new BackendStack(
    app,
    "scala-project-backend",
    StackProps.builder()
      .env(environment)
      .build()
  )
  
  // Create the frontend hosting stack
  val frontendStack = new FrontendStack(
    app,
    "scala-project-frontend", 
    StackProps.builder()
      .env(environment)
      .build()
  )

  app.synth()
}