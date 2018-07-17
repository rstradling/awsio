package com.github.rstradling.awsio.sqs

import software.amazon.awssdk.services.sqs.SQSAsyncClient
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest
import software.amazon.awssdk.services.sqs.model.Message

/**
  * Type class for defining a way to read from SQS in a loop
  * @tparam F - The effect type to use
  * @tparam A - The type of value received from SQS (Typically a sqs.Message but could be enriched if need be
  * @tparam S - The stream type to use like fs2.Stream or Monix.Iterant
  */
trait ReceiveLoop[F[_], A, S[F[_], A]] {
  def receive(client: SQSAsyncClient,
              messageOps: MessageOps[F],
              receiveMessageRequest: ReceiveMessageRequest): S[F, A]
}

/**
  * Type class for definining a way to read from SQS in a loop AND ack successfully processed messages
  * @tparam F - The effect type to use
  * @tparam A - The type of the value received from the ReceiveLoop.  (Typically a sqs.Message but could be enriched if need be
  * @tparam B - The type of the value received from the handler function.
  * @tparam S - The stream type to use like fs2.Stream or Monix.Iterant
  */
trait AckProcessor[F[_], A, B, S[F[_], B]] {
  def processAndAck(client: SQSAsyncClient,
                    messageOps: MessageOps[F],
                    queueUrl: String,
                    receiveMessageRequest: ReceiveMessageRequest,
                    handler: Message => Either[Throwable, B])(implicit receiveLoop: ReceiveLoop[F, A, S]): S[F, B]
}



