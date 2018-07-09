package com.strad.awsio.sqs

import software.amazon.awssdk.services.sqs.model._

trait QueueOps[F[_]] {
  def create(queueRequest: CreateQueueRequest): F[CreateQueueResponse]
  def list(listQueuesRequest: ListQueuesRequest): F[ListQueuesResponse]
  def getUrl(getQueueUrlRequest: GetQueueUrlRequest): F[GetQueueUrlResponse]
  def delete(deleteQueueRequest: DeleteQueueRequest): F[DeleteQueueResponse]
}
