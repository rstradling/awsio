package com.strad.awsio.s3

import cats.~>
import cats.effect.Async
import cats.implicits._
import java.util.concurrent.CompletableFuture
import software.amazon.awssdk.services.s3.S3AsyncClient
import software.amazon.awssdk.services.s3.model._

class BucketOpsAwsImpl[F[_]](client: S3AsyncClient)(implicit f: Async[F], transform: CompletableFuture ~> F) extends BucketOps [F] {
  def create(createBucketRequest: CreateBucketRequest): F[CreateBucketResponse] = {
    transform(client.createBucket(createBucketRequest))
  }
  def list(listBucketsRequest: ListBucketsRequest): F[ListBucketsResponse] = {
    transform(client.listBuckets(listBucketsRequest))
  }
  def delete(deleteBucketRequest: DeleteBucketRequest): F[DeleteBucketResponse] = {
    transform(client.deleteBucket(deleteBucketRequest))
  }
  def exists(headBucketRequest: HeadBucketRequest): F[Option[HeadBucketResponse]] = {
    transform(client.headBucket(headBucketRequest)).map(x => x.some).recover {
      case e: NoSuchBucketException =>
        None: Option[HeadBucketResponse]
    }
  }
}
