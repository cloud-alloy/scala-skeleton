package infrastructure

import software.amazon.awscdk.{RemovalPolicy, Stack, StackProps, CfnOutput, CfnOutputProps}
import software.amazon.awscdk.services.s3.{Bucket, BucketAccessControl}
import software.amazon.awscdk.services.s3.deployment.{BucketDeployment, Source}
import software.amazon.awscdk.services.cloudfront.{CloudFrontWebDistribution, SourceConfiguration, Behavior, CloudFrontAllowedMethods, CfnDistribution}
import software.constructs.Construct
import scala.jdk.CollectionConverters._

class FrontendStack(scope: Construct, id: String, props: StackProps) extends Stack(scope, id, props) {

  // Create S3 bucket for hosting the frontend
  val websiteBucket = Bucket.Builder.create(this, "FrontendBucket")
    .websiteIndexDocument("index.html")
    .websiteErrorDocument("index.html")
    .publicReadAccess(true)
    .accessControl(BucketAccessControl.BUCKET_OWNER_FULL_CONTROL)
    .removalPolicy(RemovalPolicy.DESTROY) // For dev - use RETAIN for production
    .build()

  // Deploy frontend files to S3 (assumes built files are in frontend/dist)
  val deployment = BucketDeployment.Builder.create(this, "FrontendDeployment")
    .sources(List(Source.asset("frontend/dist")).asJava)
    .destinationBucket(websiteBucket)
    .build()

  // Create CloudFront distribution for global CDN
  val distribution = CloudFrontWebDistribution.Builder.create(this, "FrontendDistribution")
    .originConfigs(List(
      SourceConfiguration.builder()
        .s3OriginSource(software.amazon.awscdk.services.cloudfront.S3OriginConfig.builder()
          .s3BucketSource(websiteBucket)
          .build())
        .behaviors(List(
          Behavior.builder()
            .isDefaultBehavior(true)
            .compress(true)
            .allowedMethods(CloudFrontAllowedMethods.GET_HEAD_OPTIONS)
            .build()
        ).asJava)
        .build()
    ).asJava)
    .errorConfigurations(List(
      CfnDistribution.CustomErrorResponseProperty.builder()
        .errorCode(404)
        .responseCode(200)
        .responsePagePath("/index.html")
        .build()
    ).asJava)
    .build()

  // Output the CloudFront URL
  new CfnOutput(this, "FrontendUrl",
    CfnOutputProps.builder()
      .value(s"https://${distribution.getDistributionDomainName()}")
      .build())
      
  // Output the S3 website URL  
  new CfnOutput(this, "S3WebsiteUrl",
    CfnOutputProps.builder()
      .value(websiteBucket.getBucketWebsiteUrl())
      .build())
}