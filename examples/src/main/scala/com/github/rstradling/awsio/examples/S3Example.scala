package com.github.rstradling.awsio.examples

import cats.MonadError
import cats.effect.ExitCode
import cats.effect.IO
import cats.effect.IOApp
import com.github.rstradling.awsio.s3.BucketOpsAwsImpl
import com.github.rstradling.awsio.s3.BucketOps
import software.amazon.awssdk.services.s3.S3AsyncClient
import software.amazon.awssdk.services.s3.model.ListBucketsRequest
import scala.jdk.CollectionConverters._
import scala.concurrent.duration._
import software.amazon.awssdk.services.s3.model.HeadBucketRequest
import software.amazon.awssdk.services.s3.model.CreateBucketRequest
import software.amazon.awssdk.services.s3.model.DeleteBucketRequest
import software.amazon.awssdk.services.s3.model.HeadBucketResponse

object S3Example extends IOApp {
  import com.github.rstradling.awsio.util.Transformations._
  val builder = S3AsyncClient.builder().build
  val bucket: BucketOps[IO] = new BucketOpsAwsImpl[IO](builder)

  def listBuckets(): IO[Unit] = {
    val listBucketRequest = ListBucketsRequest
      .builder()
      .build()

    for {
      buckets <- bucket.list(listBucketRequest)
      _ = buckets.buckets().asScala.foreach(println)
    } yield ()
  }

  private def bucketExistsError[F[_]](item: Option[HeadBucketResponse])(implicit f: MonadError[F, Throwable]): F[Unit] = {
    item match {
      case Some(_) => f.raiseError(new RuntimeException("Bucket should not exist"))
      case None    => f.pure(())
    }
  }
  private def bucketDoesNotExistError[F[_]](item: Option[HeadBucketResponse])(implicit f: MonadError[F, Throwable]): F[Unit] = {
    item match {
      case Some(_) => f.pure(())
      case None => f.raiseError(new RuntimeException("Bucket should exist"))
    }
  }
  def createDeleteBucket(): IO[Unit] = {
    val bucketName = "boomtown-test-foo"
    val bucketRequest = HeadBucketRequest.builder().bucket(bucketName).build()
    val createBucketRequest =
      CreateBucketRequest.builder().bucket(bucketName).build()
    val deleteBucketRequest =
      DeleteBucketRequest.builder().bucket(bucketName).build()
    for {
      _ <- bucket.exists(bucketRequest).map(bucketExistsError[IO])
      _ <- bucket.create(createBucketRequest)
      _ <- bucket.exists(bucketRequest).map(bucketDoesNotExistError[IO])
      _ <- bucket.delete(deleteBucketRequest)
      _ = IO.sleep(5.seconds) // S3 eventual consistency sleep :)
      _ <- bucket.exists(bucketRequest).map(bucketExistsError[IO])
    } yield ()
  }

  def run(args: List[String]): IO[ExitCode] = {
    for {
        _ <- listBuckets()
        _ <- createDeleteBucket()
    } yield ExitCode.Success
  }
}
