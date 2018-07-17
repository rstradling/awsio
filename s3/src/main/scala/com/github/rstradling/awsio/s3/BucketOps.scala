package com.github.rstradling.awsio.s3

import software.amazon.awssdk.services.s3.model._

/**
  * Final tagless style of S3 operations on a S3 bucket
  * @tparam F - The effect type to use like Monix Task or IO
  */
trait BucketOps[F[_]] {
  def create(createBucketRequest: CreateBucketRequest): F[CreateBucketResponse]
  def list(listBucketsRequest: ListBucketsRequest): F[ListBucketsResponse]
  def delete(deleteBucketRequest: DeleteBucketRequest): F[DeleteBucketResponse]
  def exists(headBucketRequest: HeadBucketRequest): F[Option[HeadBucketResponse]]
}
