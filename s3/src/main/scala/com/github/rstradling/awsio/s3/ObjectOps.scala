package com.github.rstradling.awsio.s3

import software.amazon.awssdk.core.async.AsyncRequestBody
import software.amazon.awssdk.core.async.AsyncResponseTransformer
import software.amazon.awssdk.services.s3.model._

/**
  * Final tagless style of S3 operations on an s3 file.
  *
  * Note: All of these operations need more work to get it over to fs2 implementation
  * @tparam F - The effect type to use like Monix Task or IO
  */
trait ObjectOps[F[_]] {
  def put(putObjectRequest: PutObjectRequest,
          body: AsyncRequestBody): F[PutObjectResponse]
  def putMultipart(uploadPartRequest: UploadPartRequest,
                   body: AsyncRequestBody): F[UploadPartResponse]
  def get(getObjectRequest: GetObjectRequest,
          asyncResponseTransformer: AsyncResponseTransformer[GetObjectResponse,
                                                             GetObjectResponse])
    : F[GetObjectResponse]
  def delete(DeleteObjectRequest: DeleteObjectRequest): F[DeleteObjectResponse]
}
