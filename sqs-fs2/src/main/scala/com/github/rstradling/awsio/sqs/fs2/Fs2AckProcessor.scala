package com.github.rstradling.awsio.sqs.fs2

import cats.effect.Effect
import cats.implicits._
import com.github.rstradling.awsio.sqs.ReceiveLoop
import com.github.rstradling.awsio.sqs.MessageOps
import com.github.rstradling.awsio.sqs.AckProcessor
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest
import software.amazon.awssdk.services.sqs.model.Message
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest

/**
  * Implements the AckProcessor typeclass that will read messages from SQS run the handler and ack ones
  * that return a Right() value in the Either.  It uses fs2.Stream for the stream processing
  */
object Fs2AckProcessor {

  /**
    *
    * @param messageOps - The message ops to use
    * @param queueUrl - The queueUrl to use for acking/deleting the message
    * @param receiveMessageRequest - The message request
    * @param handler - The handler for the message that returns an Either[Throwable, B]
    * @param p - The AckProcessor
    * @tparam F - Effect type that must implement cats.Effect
    * @tparam B
    * @return
    */
  def process[F[_]: Effect, B](messageOps: MessageOps[F],
                       queueUrl: String,
                       receiveMessageRequest: ReceiveMessageRequest,
                       handler: Message => Either[Throwable, B])(
      implicit p: AckProcessor[F, Message, B, fs2.Stream]): fs2.Stream[F, B] = {
    p.processAndAck(messageOps,
                    queueUrl,
                    receiveMessageRequest,
                    handler)(Fs2ReceiveLoop.receiveLoop[F])
  }

  private def deleteMessage[F[_]: Effect](m: Message,
                                         messageOps: MessageOps[F],
                                         queueUrl: String): F[Unit] = {
    val deleteMessageRequest = DeleteMessageRequest
      .builder()
      .queueUrl(queueUrl)
      .receiptHandle(m.receiptHandle())
      .build
    for {
      deleteResponse <- messageOps.delete(deleteMessageRequest)
    } yield ()
  }
  implicit def ackProcessor[F[_]: Effect, B]
    : AckProcessor[F, Message, B, fs2.Stream] =
    new AckProcessor[F, Message, B, fs2.Stream] {
      def processAndAck(messageOps: MessageOps[F],
                        queueUrl: String,
                        receiveMessageRequest: ReceiveMessageRequest,
                        handler: Message => Either[Throwable, B])(
          implicit receiveLoop: ReceiveLoop[F, Message, fs2.Stream])
        : fs2.Stream[F, B] = {
        receiveLoop.receive(messageOps, receiveMessageRequest).flatMap {
          msg =>
            handler(msg).fold(
              (t => fs2.Stream.raiseError(t)), { item =>
                val ret = for {
                  del <- deleteMessage(msg, messageOps, queueUrl)
                  res <- implicitly[Effect[F]].pure(item)
                } yield res
                fs2.Stream.eval(ret)
              }
            )
        }
      }
    }
}
