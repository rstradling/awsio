package com.github.rstradling.awsio.s3

import software.amazon.awssdk.services.s3.model._

trait BucketOps[F[_]] {
  def create(createBucketRequest: CreateBucketRequest): F[CreateBucketResponse]
  def list(listBucketsRequest: ListBucketsRequest): F[ListBucketsResponse]
  def delete(deleteBucketRequest: DeleteBucketRequest): F[DeleteBucketResponse]
  def exists(headBucketRequest: HeadBucketRequest): F[Option[HeadBucketResponse]]
}
