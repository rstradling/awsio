package com.github.rstradling.awsio.sqs.fs2

import cats.effect.Async
import com.github.rstradling.awsio.sqs.ReceiveLoop
import com.github.rstradling.awsio.sqs.MessageOps
import scala.collection.JavaConverters._
import software.amazon.awssdk.services.sqs.SQSAsyncClient
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest
import software.amazon.awssdk.services.sqs.model.Message

object Fs2ReceiveLoop {
  def receive[F[_], A, S[F[_], A]](
      client: SQSAsyncClient,
      messageOps: MessageOps[F],
      receiveMessageRequest: ReceiveMessageRequest)(
      implicit r: ReceiveLoop[F, A, S]): S[F, A] = {
    r.receive(client, messageOps, receiveMessageRequest)
  }
  implicit def receiveLoop[F[_]: Async]: ReceiveLoop[F, Message, fs2.Stream] =
    new ReceiveLoop[F, Message, fs2.Stream] {
      def receive(client: SQSAsyncClient,
                  messageOps: MessageOps[F],
                  receiveMessageRequest: ReceiveMessageRequest)
        : fs2.Stream[F, Message] = {
        val f = implicitly[Async[F]]
        for {
          _  <- fs2.Stream.repeatEval(f.pure(()))
          m  <- fs2.Stream.eval(messageOps.receive(receiveMessageRequest))
          message <- if (m != null && m.messages != null && m.messages().size > 0) {
            fs2.Stream.emits(m.messages().asScala)
          } else {
            fs2.Stream.empty
          }
        } yield (message)
      }
    }
}
