package com.github.rstradling.awsio.sqs

import cats.effect.Async
import cats.~>
import java.util.concurrent.CompletableFuture
import software.amazon.awssdk.services.sqs.SqsAsyncClient
import software.amazon.awssdk.services.sqs.model._

class QueueOpsAwsImpl[F[_]](client: SqsAsyncClient)(implicit f: Async[F], transform: CompletableFuture ~> F) extends QueueOps[F] {
  def create(queueRequest: CreateQueueRequest): F[CreateQueueResponse] = {
    transform(client.createQueue(queueRequest))
  }
  def list(listQueuesRequest: ListQueuesRequest): F[ListQueuesResponse] = {
    transform(client.listQueues(listQueuesRequest))
  }
  def getUrl(getQueueUrlRequest: GetQueueUrlRequest): F[GetQueueUrlResponse] = {
    transform(client.getQueueUrl(getQueueUrlRequest))
  }
  def delete(deleteQueueRequest: DeleteQueueRequest): F[DeleteQueueResponse] = {
    transform(client.deleteQueue(deleteQueueRequest))
  }
}
