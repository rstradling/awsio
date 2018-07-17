package com.github.rstradling.awsio.sqs

import software.amazon.awssdk.services.sqs.model._

/**
  * Final tagless style of sqs queue operations
  * @tparam F - The effect type to use like Monix.Task or cats.effect.IO
  */
trait QueueOps[F[_]] {
  def create(queueRequest: CreateQueueRequest): F[CreateQueueResponse]
  def list(listQueuesRequest: ListQueuesRequest): F[ListQueuesResponse]
  def getUrl(getQueueUrlRequest: GetQueueUrlRequest): F[GetQueueUrlResponse]
  def delete(deleteQueueRequest: DeleteQueueRequest): F[DeleteQueueResponse]
}
