package com.strad.awsio.examples

import cats.effect.IO
import com.strad.awsio.s3.BucketOps
import com.strad.awsio.s3.BucketOpsAwsImpl
import com.strad.awsio.util.Transformations._
import software.amazon.awssdk.services.s3.S3AsyncClient
import software.amazon.awssdk.services.s3.model.ListBucketsRequest
import scala.collection.JavaConverters._
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import software.amazon.awssdk.services.s3.model.HeadBucketRequest
import software.amazon.awssdk.services.s3.model.CreateBucketRequest
import software.amazon.awssdk.services.s3.model.DeleteBucketRequest
import software.amazon.awssdk.services.s3.model.HeadBucketResponse

object S3Example extends App {
  val builder = S3AsyncClient.builder().build
  val bucket: BucketOps[IO] = new BucketOpsAwsImpl[IO](builder)

  def listBuckets(): Unit = {
    val listBucketRequest = ListBucketsRequest
      .builder()
      .build()

    val ret = for {
      buckets <- bucket.list(listBucketRequest)
      _ = buckets.buckets().asScala.foreach(println)
    } yield ()
    ret.unsafeRunTimed(10.seconds)
  }

  private def bucketExistsError(item: Option[HeadBucketResponse]): Unit = {
    item match {
      case Some(_) => throw new RuntimeException("Bucket should not exist")
      case None    => ()
    }
  }
  private def bucketDoesNotExistError(item: Option[HeadBucketResponse]): Unit = {
    item match {
      case Some(_) => ()
      case None => throw new RuntimeException("Bucket should exist")
    }
  }
  def createDeleteBucket(): Unit = {
    val bucketName = "boomtown-test-foo"
    val bucketRequest = HeadBucketRequest.builder().bucket(bucketName).build()
    val createBucketRequest =
      CreateBucketRequest.builder().bucket(bucketName).build()
    val deleteBucketRequest =
      DeleteBucketRequest.builder().bucket(bucketName).build()
    val ret = for {
      _ <- bucket.exists(bucketRequest).map(bucketExistsError)
      _ <- bucket.create(createBucketRequest)
      _ <- bucket.exists(bucketRequest).map(bucketDoesNotExistError)
      _ <- bucket.delete(deleteBucketRequest)
      _ = IO.sleep(5.seconds) // S3 eventual consistency sleep :)
      _ <- bucket.exists(bucketRequest).map(bucketExistsError)
    } yield ()
    ret.unsafeRunTimed(10.seconds)
  }

  listBuckets()
  createDeleteBucket()
}
