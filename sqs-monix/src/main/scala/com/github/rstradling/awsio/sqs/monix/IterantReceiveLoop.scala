package com.github.rstradling.awsio.sqs.monix

import cats.effect.Async
import com.github.rstradling.awsio.sqs.MessageOps
import com.github.rstradling.awsio.sqs.ReceiveLoop
import com.github.rstradling.awsio.sqs.MessageOps
import com.github.rstradling.awsio.sqs.ReceiveLoop
import monix.tail.Iterant
import scala.collection.JavaConverters._
import software.amazon.awssdk.services.sqs.SQSAsyncClient
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest
import software.amazon.awssdk.services.sqs.model.Message
import software.amazon.awssdk.services.sqs.model.ReceiveMessageResponse

object IterantReceiveLoop {
  def receive[F[_], A, S[F[_], A]](
      client: SQSAsyncClient,
      messageOps: MessageOps[F],
      receiveMessageRequest: ReceiveMessageRequest)(
      implicit r: ReceiveLoop[F, A, S]): S[F, A] = {
    r.receive(messageOps, receiveMessageRequest)
  }
  implicit def receiveLoop[F[_]: Async]: ReceiveLoop[F, Message, Iterant] =
    new ReceiveLoop[F, Message, Iterant] {
      def receive(
          messageOps: MessageOps[F],
          receiveMessageRequest: ReceiveMessageRequest): Iterant[F, Message] = {
        val results: ReceiveMessageResponse => Iterant[F, Message] = { a =>
          Iterant.fromList[F, Message](a.messages().asScala.toList)
        }
        Iterant
          .repeatEvalF(messageOps.receive(receiveMessageRequest))
          .flatMap(results)
      }
    }
}
