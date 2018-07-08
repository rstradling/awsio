package com.strad.awsio.s3

import cats.~>
import cats.effect.Async
import java.util.concurrent.CompletableFuture
import software.amazon.awssdk.core.async.AsyncRequestBody
import software.amazon.awssdk.core.async.AsyncResponseTransformer
import software.amazon.awssdk.services.s3.S3AsyncClient
import software.amazon.awssdk.services.s3.model.CreateBucketRequest
import software.amazon.awssdk.services.s3.model.CreateBucketResponse
import software.amazon.awssdk.services.s3.model.DeleteBucketRequest
import software.amazon.awssdk.services.s3.model.DeleteBucketResponse
import software.amazon.awssdk.services.s3.model.ListBucketsRequest
import software.amazon.awssdk.services.s3.model.ListBucketsResponse
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import software.amazon.awssdk.services.s3.model.GetObjectRequest
import software.amazon.awssdk.services.s3.model.DeleteObjectResponse
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest
import software.amazon.awssdk.services.s3.model.PutObjectResponse
import software.amazon.awssdk.services.s3.model.GetObjectResponse
import software.amazon.awssdk.services.s3.model.UploadPartRequest
import software.amazon.awssdk.services.s3.model.UploadPartResponse

trait BucketOps[F[_]] {
  def create(createBucketRequest: CreateBucketRequest): F[CreateBucketResponse]
  def list(listBucketsjRequest: ListBucketsRequest): F[ListBucketsResponse]
  def delete(deleteBucketRequest: DeleteBucketRequest): F[DeleteBucketResponse]
}

trait ObjectOps[F[_]] {
  def put(putObjectRequest: PutObjectRequest, body: AsyncRequestBody): F[PutObjectResponse]
  def putMultipart(uploadPartRequest: UploadPartRequest, body: AsyncRequestBody): F[UploadPartResponse]
  def get(getObjectRequest: GetObjectRequest, asyncResponseTransformer: AsyncResponseTransformer[GetObjectResponse,GetObjectResponse]): F[GetObjectResponse]
  def delete(DeleteObjectRequest: DeleteObjectRequest): F[DeleteObjectResponse]
}



class BucketOpsImpl[F[_]](client: S3AsyncClient)(implicit f: Async[F], transform: CompletableFuture ~> F) extends BucketOps [F] {
  def create(createBucketRequest: CreateBucketRequest): F[CreateBucketResponse] = {
    transform(client.createBucket(createBucketRequest))
  }
  def list(listBucketsRequest: ListBucketsRequest): F[ListBucketsResponse] = {
    transform(client.listBuckets(listBucketsRequest))
  }
  def delete(deleteBucketRequest: DeleteBucketRequest): F[DeleteBucketResponse] = {
    transform(client.deleteBucket(deleteBucketRequest))
  }
}

class ObjectOpsImpl[F[_]](client: S3AsyncClient)(implicit f: Async[F], transform: CompletableFuture ~> F) extends ObjectOps [F] {
  def put(putObjectRequest: PutObjectRequest, body: AsyncRequestBody): F[PutObjectResponse] = {
    transform(client.putObject(putObjectRequest, body))

  }
  def putMultipart(uploadPartRequest: UploadPartRequest, body: AsyncRequestBody): F[UploadPartResponse] = {
    transform(client.uploadPart(uploadPartRequest, body))

  }
  def get(getObjectRequest: GetObjectRequest, asyncResponseTransformer: AsyncResponseTransformer[GetObjectResponse, GetObjectResponse]): F[GetObjectResponse] = {
    transform(client.getObject(getObjectRequest, asyncResponseTransformer))
  }
  def delete(deleteObjectRequest: DeleteObjectRequest): F[DeleteObjectResponse] = {
    transform(client.deleteObject(deleteObjectRequest))
  }
}
