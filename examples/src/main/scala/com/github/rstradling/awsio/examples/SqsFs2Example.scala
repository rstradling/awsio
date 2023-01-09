package com.github.rstradling.awsio.examples

import cats.effect.ExitCode
import cats.effect.IO
import cats.effect.IOApp
import com.github.rstradling.awsio.sqs.QueueOps
import com.github.rstradling.awsio.sqs.MessageOps
import com.github.rstradling.awsio.sqs.QueueOpsAwsImpl
import com.github.rstradling.awsio.sqs.MessageOpsAwsImpl
import com.github.rstradling.awsio.sqs.AckProcessor
import com.github.rstradling.awsio.sqs.ReceiveLoop
import com.github.rstradling.awsio.sqs.fs2.Fs2AckProcessor
import com.github.rstradling.awsio.sqs.fs2.Fs2ReceiveLoop
import scala.concurrent.duration._
import software.amazon.awssdk.services.sqs.SqsAsyncClient
import software.amazon.awssdk.services.sqs.model.CreateQueueRequest
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest
import software.amazon.awssdk.services.sqs.model.DeleteQueueRequest
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest
import software.amazon.awssdk.services.sqs.model.SendMessageRequest
import software.amazon.awssdk.services.sqs.model.Message

object SqsFs2Example extends IOApp {
  import com.github.rstradling.awsio.util.Transformations._
  val builder = SqsAsyncClient.builder().build
  val sqs: QueueOps[IO] = new QueueOpsAwsImpl[IO](builder)
  val message: MessageOps[IO] = new MessageOpsAwsImpl[IO](builder)

  def publish(implicit processAndAck: AckProcessor[IO, Message, Unit, fs2.Stream],
              receiveLoop:ReceiveLoop[IO, Message, fs2.Stream]): IO[(Unit, Unit)] = {
    val qName = "strad-test-queue"
    val createReq = CreateQueueRequest.builder.queueName(qName).build
    val urlRequest = GetQueueUrlRequest.builder.queueName(qName).build
    for {
      _ <- sqs.create(createReq)
      urlResp <- sqs.getUrl(urlRequest)
      _ = DeleteQueueRequest.builder.queueUrl(urlResp.queueUrl()).build()
      messageRequest = ReceiveMessageRequest.builder().queueUrl(urlResp.queueUrl()).waitTimeSeconds(2).maxNumberOfMessages(10).build
      sendMessageRequest = SendMessageRequest.builder().queueUrl(urlResp.queueUrl())
        .messageBody("MyBody")
        .build
      _ <- message.send(sendMessageRequest)
      acker <- Fs2AckProcessor.process(message, urlResp.queueUrl(), messageRequest, {(m : Message) =>
        println(m)
        Right(()): Either[Throwable, Unit]
      }).compile.drain

    } yield ((), acker)
  }
  def run(args: List[String]): IO[ExitCode] = {
    publish(Fs2AckProcessor.ackProcessor, Fs2ReceiveLoop.receiveLoop).map(_ => ExitCode.Success).timeout(10.seconds)
  }

}
