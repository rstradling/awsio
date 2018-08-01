package com.github.rstradling.awsio.sqs

import cats.~>
import cats.effect.Effect
import java.util.concurrent.CompletableFuture
import software.amazon.awssdk.services.sqs.SQSAsyncClient
import software.amazon.awssdk.services.sqs.model._

class MessageOpsAwsImpl[F[_]](client: SQSAsyncClient)(
    implicit f: Effect[F],
    transform: CompletableFuture ~> F)
    extends MessageOps[F] {
  def send(sendMessageRequest: SendMessageRequest): F[SendMessageResponse] = {
    transform(client.sendMessage(sendMessageRequest))
  }
  def sendBatch(sendMessageBatchRequest: SendMessageBatchRequest)
    : F[SendMessageBatchResponse] = {
    transform(client.sendMessageBatch(sendMessageBatchRequest))
  }
  def receive(receiveMessageRequest: ReceiveMessageRequest)
    : F[ReceiveMessageResponse] = {
    transform(client.receiveMessage(receiveMessageRequest))
  }
  def delete(
      deleteMessageRequest: DeleteMessageRequest): F[DeleteMessageResponse] = {
    transform(client.deleteMessage(deleteMessageRequest))
  }
}
