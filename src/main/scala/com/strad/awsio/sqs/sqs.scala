package com.strad.awsio.sqs

import cats.~>
import cats.effect.Async
import java.util.concurrent.CompletableFuture
import software.amazon.awssdk.services.sqs.SQSAsyncClient
import software.amazon.awssdk.services.sqs.model.CreateQueueRequest
import software.amazon.awssdk.services.sqs.model.CreateQueueResponse
import software.amazon.awssdk.services.sqs.model.ListQueuesRequest
import software.amazon.awssdk.services.sqs.model.ListQueuesResponse
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest
import software.amazon.awssdk.services.sqs.model.GetQueueUrlResponse
import software.amazon.awssdk.services.sqs.model.DeleteQueueRequest
import software.amazon.awssdk.services.sqs.model.DeleteQueueResponse
import software.amazon.awssdk.services.sqs.model.SendMessageRequest
import software.amazon.awssdk.services.sqs.model.SendMessageResponse
import software.amazon.awssdk.services.sqs.model.SendMessageBatchRequest
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest
import software.amazon.awssdk.services.sqs.model.ReceiveMessageResponse
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest
import software.amazon.awssdk.services.sqs.model.DeleteMessageResponse
import software.amazon.awssdk.services.sqs.model.SendMessageBatchResponse

trait QueueOps[F[_]] {
  def create(queueRequest: CreateQueueRequest): F[CreateQueueResponse]
  def list(listQueuesRequest: ListQueuesRequest): F[ListQueuesResponse]
  def getUrl(getQueueUrlRequest: GetQueueUrlRequest): F[GetQueueUrlResponse]
  def delete(deleteQueueRequest: DeleteQueueRequest): F[DeleteQueueResponse]
}

trait MessageOps[F[_]] {
  def send(sendMessageRequest: SendMessageRequest): F[SendMessageResponse]
  def sendBatch(sendMessageBatchRequest: SendMessageBatchRequest): F[SendMessageBatchResponse]
  def receive(receiveMessageRequest: ReceiveMessageRequest): F[ReceiveMessageResponse]
  def delete(deleteMessageRequest: DeleteMessageRequest): F[DeleteMessageResponse]
}


class QueueOpsImpl[F[_]](client: SQSAsyncClient)(implicit f: Async[F], transform: CompletableFuture ~> F) extends QueueOps[F] {
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


class MessageOpsImpl[F[_]](client: SQSAsyncClient)(implicit f: Async[F], transform: CompletableFuture ~> F) extends MessageOps[F] {
  def send(sendMessageRequest: SendMessageRequest): F[SendMessageResponse] = {
    transform(client.sendMessage(sendMessageRequest))
  }
  def sendBatch(sendMessageBatchRequest: SendMessageBatchRequest): F[SendMessageBatchResponse] = {
    transform(client.sendMessageBatch(sendMessageBatchRequest))
  }
  def receive(receiveMessageRequest: ReceiveMessageRequest): F[ReceiveMessageResponse] = {
    transform(client.receiveMessage(receiveMessageRequest))
  }
  def delete(deleteMessageRequest: DeleteMessageRequest): F[DeleteMessageResponse] = {
    transform(client.deleteMessage(deleteMessageRequest))
  }
}

