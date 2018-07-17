package com.github.rstradling.awsio.sqs

import software.amazon.awssdk.services.sqs.model._

/**
  * Final tagless style for sqs message operations
  * @tparam F - The effect type to use
  */
trait MessageOps[F[_]] {
  def send(sendMessageRequest: SendMessageRequest): F[SendMessageResponse]
  def sendBatch(sendMessageBatchRequest: SendMessageBatchRequest): F[SendMessageBatchResponse]
  def receive(receiveMessageRequest: ReceiveMessageRequest): F[ReceiveMessageResponse]
  def delete(deleteMessageRequest: DeleteMessageRequest): F[DeleteMessageResponse]
}
