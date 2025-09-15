#!/bin/bash
set -x

# Create S3 bucket in LocalStack
aws --endpoint-url=http://localhost:4566 s3 mb s3://product-images-bucket --region us-east-1

# List S3 buckets to verify creation
aws --endpoint-url=http://localhost:4566 s3 ls
set +x
