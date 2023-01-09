package com.github.rstradling.awsio.sqs.fs2

import cats.effect.Async
import com.github.rstradling.awsio.sqs.ReceiveLoop
import com.github.rstradling.awsio.sqs.MessageOps
import scala.jdk.CollectionConverters._
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest
import software.amazon.awssdk.services.sqs.model.Message

/**
  * Fs2 based processing of sqs messages
  */
object Fs2ReceiveLoop {
  /**
    * Does a receive of a sqs message
    * @param messageOps - Message operations
    * @param receiveMessageRequest - Message request
    * @param r - ReceiveLoop typeclass implementation
    * @tparam F - Effect type to use
    * @tparam A - Input message type (i.e. aws....Message)
    * @tparam S - The stream to use
    * @return - A Stream of As using effect F
    */
  def receive[F[_], A, S[F[_], A]](
      messageOps: MessageOps[F],
      receiveMessageRequest: ReceiveMessageRequest)(
      implicit r: ReceiveLoop[F, A, S]): S[F, A] = {
    r.receive(messageOps, receiveMessageRequest)
  }
  implicit def receiveLoop[F[_]: Async]: ReceiveLoop[F, Message, fs2.Stream] =
    new ReceiveLoop[F, Message, fs2.Stream] {
      def receive(messageOps: MessageOps[F],
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
