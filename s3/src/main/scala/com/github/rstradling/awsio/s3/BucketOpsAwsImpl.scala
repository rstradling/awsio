package com.github.rstradling.awsio.s3

import cats.{~>, ApplicativeError}
import cats.syntax.applicativeError._
import cats.syntax.functor._
import cats.syntax.option._
import java.util.concurrent.CompletableFuture
import software.amazon.awssdk.services.s3.S3AsyncClient
import software.amazon.awssdk.services.s3.model._

/**
  * Implementation of the aws bucket operations using an S3 async client.
  * This client uses a Java 8 CompleteableFuture
  * @param client - The s3 Async client
  * @param F - F[_] must support cats.ApplicativeError[F, Throwable] for .recover
  * @param transform - Natural transformation from a CompletableFuture to an F
  * @tparam F - The effect type to use like Monix Task or IO
  */
class BucketOpsAwsImpl[F[_]](client: S3AsyncClient)(implicit F: ApplicativeError[F, Throwable], transform: CompletableFuture ~> F) extends BucketOps[F] {
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
      // not exist. That does not seem to be true and instead a 404 S3Exception is thrown
      case _: NoSuchBucketException => none[HeadBucketResponse]
      case s: S3Exception if s.statusCode == 404 => none[HeadBucketResponse]
    }
  }
}
