package infrastructure

import software.amazon.awscdk.{Duration, Stack, StackProps, CfnOutput, CfnOutputProps}
import software.amazon.awscdk.services.lambda.{Code, Function, Runtime}
import software.amazon.awscdk.services.apigateway.{LambdaRestApi, CorsOptions, Cors}
import software.constructs.Construct
import scala.jdk.CollectionConverters._

class BackendStack(scope: Construct, id: String, props: StackProps) extends Stack(scope, id, props) {
  
  private val scalaVersion: String = 
    BuildInfo.scalaVersion.split("""\.""").take(2).mkString(".")

  // Create the Lambda function for the backend
  val backendFunction = Function.Builder.create(this, "BackendFunction")
    .runtime(Runtime.JAVA_17)
    .timeout(Duration.seconds(30))
    .memorySize(1024)
    .handler(BuildInfo.backendHandler)
    .code(Code.fromAsset(
      s"${BuildInfo.backendDir}/target/scala-$scalaVersion/${BuildInfo.backendJarName}"
    ))
    .build()

  // Create API Gateway to expose the Lambda
  val api = LambdaRestApi.Builder.create(this, "BackendApi")
    .handler(backendFunction)
    .defaultCorsPreflightOptions(CorsOptions.builder()
      .allowOrigins(Cors.ALL_ORIGINS)
      .allowMethods(Cors.ALL_METHODS)
      .allowHeaders(List("Content-Type", "X-Amz-Date", "Authorization", "X-Api-Key").asJava)
      .build())
    .build()

  // Output the API endpoint URL
  new CfnOutput(this, "ApiEndpoint", 
    CfnOutputProps.builder()
      .value(api.getUrl())
      .build())
}