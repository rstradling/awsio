package com.github.rstradling.awsio.sqs

import cats.effect.Async
import cats.~>
import java.util.concurrent.CompletableFuture
import software.amazon.awssdk.services.sqs.model._
import software.amazon.awssdk.services.sqs.SqsAsyncClient

class MessageOpsAwsImpl[F[_]](client: SqsAsyncClient)(implicit f: Async[F], transform: CompletableFuture ~> F) extends MessageOps[F] {
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

