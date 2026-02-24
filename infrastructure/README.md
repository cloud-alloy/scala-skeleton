# Infrastructure Deployment Guide

This project now includes infrastructure as code using AWS CDK written in Scala, allowing you to deploy your full-stack application to AWS.

## Prerequisites

1. **AWS CLI configured** with your credentials:
   ```bash
   aws configure
   ```

2. **AWS CDK installed**:
   ```bash
   npm install -g aws-cdk
   ```

3. **Bootstrap your AWS account** (one-time setup):
   ```bash
   cdk bootstrap
   ```

## Infrastructure Components

### Backend Stack (`BackendStack.scala`)
- **AWS Lambda**: Hosts your Scala backend service
- **API Gateway**: Provides REST API endpoints
- **IAM Roles**: Proper permissions for Lambda execution

### Frontend Stack (`FrontendStack.scala`)  
- **S3 Bucket**: Hosts static frontend files
- **CloudFront Distribution**: Global CDN for fast content delivery
- **Deployment**: Automatic upload of built frontend files

## Deployment Commands

### 1. Build and Deploy Everything
```bash
# Build all components and deploy infrastructure
sbt backend/assembly  # Build backend JAR
sbt frontend/fastOptJS  # Build frontend (or fullOptJS for production)
cdk deploy --all
```

### 2. Deploy Individual Stacks
```bash
# Deploy only backend
cdk deploy scala-project-backend

# Deploy only frontend  
cdk deploy scala-project-frontend
```

### 3. Preview Changes
```bash
cdk diff  # See what will change
```

### 4. Destroy Infrastructure
```bash
cdk destroy --all
```

## Development Workflow

1. **Make code changes** in `backend/` or `frontend/`
2. **Test locally** using your existing development setup
3. **Build the components**:
   ```bash
   sbt backend/assembly          # For backend changes
   npm run build --prefix frontend  # For frontend changes (if using npm)
   ```
4. **Deploy changes**:
   ```bash
   cdk deploy --all
   ```

## Outputs

After deployment, CDK will output:
- **API Endpoint**: Your backend API URL
- **Frontend URL**: CloudFront distribution URL  
- **S3 Website URL**: Direct S3 website URL

## Configuration

### Environment Variables
Set these environment variables for deployment:
- `CDK_DEFAULT_ACCOUNT`: Your AWS account ID
- `CDK_DEFAULT_REGION`: Your preferred AWS region

### Build Info
The infrastructure uses SBT's BuildInfo plugin to pass build information:
- Backend JAR location
- Backend handler class
- Scala version information

## Cost Optimization

- **S3**: Pay for storage and requests
- **Lambda**: Pay per invocation (generous free tier)
- **API Gateway**: Pay per API call
- **CloudFront**: Pay for data transfer (free tier available)

For development, consider using:
- Smaller Lambda memory sizes
- S3 website hosting only (without CloudFront)
- Single region deployment

## Troubleshooting

1. **Build failures**: Ensure `sbt backend/assembly` completes successfully
2. **Permission errors**: Check AWS credentials and IAM permissions
3. **CDK bootstrap**: Required for first-time deployment in a region
4. **Frontend not updating**: Clear CloudFront cache or wait for TTL expiration

## Project Structure
```
infrastructure/
├── src/main/scala/
│   ├── CdkMain.scala        # CDK app entry point
│   ├── BackendStack.scala   # Backend infrastructure
│   └── FrontendStack.scala  # Frontend infrastructure
└── target/                  # Build artifacts
```