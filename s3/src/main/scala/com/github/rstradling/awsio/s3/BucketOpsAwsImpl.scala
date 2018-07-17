package com.github.rstradling.awsio.s3

import cats.~>
import cats.effect.Effect
import cats.implicits._
import java.util.concurrent.CompletableFuture
import software.amazon.awssdk.services.s3.S3AsyncClient
import software.amazon.awssdk.services.s3.model._

/**
  * Implementation of the aws bucket operations using an S3 async client.
  * This client uses a Java 8 CompleteableFuture
  * @param client - The s3 Async client
  * @param f - Effect must support cats.effect.Effect operations
  * @param transform - Natural transformation from a CompletableFuture to an F
  * @tparam F - The effect type to use like Monix Task or IO
  */
class BucketOpsAwsImpl[F[_]](client: S3AsyncClient)(implicit f: Effect[F], transform: CompletableFuture ~> F) extends BucketOps [F] {
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
      // Docs say a NoSuchBucketException should occur for a bucket that does
      // not exist.  That does not seem to be true and instead a 404 S3Exception
      // is thrown
      case _: NoSuchBucketException =>
        None: Option[HeadBucketResponse]
      case s: S3Exception if s.statusCode == 404 =>
        None: Option[HeadBucketResponse]
    }
  }
}
