package com.strad.awsio.s3

import software.amazon.awssdk.core.async.AsyncRequestBody
import software.amazon.awssdk.core.async.AsyncResponseTransformer
import software.amazon.awssdk.services.s3.model._

trait ObjectOps[F[_]] {
  def put(putObjectRequest: PutObjectRequest, body: AsyncRequestBody): F[PutObjectResponse]
  def putMultipart(uploadPartRequest: UploadPartRequest, body: AsyncRequestBody): F[UploadPartResponse]
  def get(getObjectRequest: GetObjectRequest, asyncResponseTransformer: AsyncResponseTransformer[GetObjectResponse,GetObjectResponse]): F[GetObjectResponse]
  def delete(DeleteObjectRequest: DeleteObjectRequest): F[DeleteObjectResponse]
}
