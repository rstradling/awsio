package com.github.rstradling.awsio.sqs

import software.amazon.awssdk.services.sqs.SQSAsyncClient
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest
import software.amazon.awssdk.services.sqs.model.Message

trait ReceiveLoop[F[_], A, S[F[_], A]] {
  def receive(client: SQSAsyncClient,
              messageOps: MessageOps[F],
              receiveMessageRequest: ReceiveMessageRequest): S[F, A]
}

trait AckProcessor[F[_], A, B, S[F[_], B]] {
  def processAndAck(client: SQSAsyncClient,
                    messageOps: MessageOps[F],
                    queueUrl: String,
                    receiveMessageRequest: ReceiveMessageRequest,
                    handler: Message => Either[Throwable, B])(implicit receiveLoop: ReceiveLoop[F, A, S]): S[F, B]
}



