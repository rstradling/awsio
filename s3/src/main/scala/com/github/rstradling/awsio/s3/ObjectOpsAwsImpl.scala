package com.github.rstradling.awsio.s3

import cats.~>
import cats.effect.Effect
import java.util.concurrent.CompletableFuture
import software.amazon.awssdk.core.async.AsyncRequestBody
import software.amazon.awssdk.core.async.AsyncResponseTransformer
import software.amazon.awssdk.services.s3.S3AsyncClient
import software.amazon.awssdk.services.s3.model._

class ObjectOpsAwsImpl[F[_]](client: S3AsyncClient)(implicit f: Effect[F], transform: CompletableFuture ~> F) extends ObjectOps [F] {
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
