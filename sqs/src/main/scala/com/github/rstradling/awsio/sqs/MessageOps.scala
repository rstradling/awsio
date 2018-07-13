package com.github.rstradling.awsio.sqs

import software.amazon.awssdk.services.sqs.model._

trait MessageOps[F[_]] {
  def send(sendMessageRequest: SendMessageRequest): F[SendMessageResponse]
  def sendBatch(sendMessageBatchRequest: SendMessageBatchRequest): F[SendMessageBatchResponse]
  def receive(receiveMessageRequest: ReceiveMessageRequest): F[ReceiveMessageResponse]
  def delete(deleteMessageRequest: DeleteMessageRequest): F[DeleteMessageResponse]
}
