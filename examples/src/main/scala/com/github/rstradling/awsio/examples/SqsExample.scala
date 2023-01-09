package com.github.rstradling.awsio.examples

import cats.effect.ExitCode
import cats.effect.IO
import cats.effect.IOApp
import com.github.rstradling.awsio.sqs.QueueOps
import com.github.rstradling.awsio.sqs.MessageOps
import com.github.rstradling.awsio.sqs.QueueOpsAwsImpl
import com.github.rstradling.awsio.sqs.MessageOpsAwsImpl
import scala.concurrent.duration._
import scala.jdk.CollectionConverters._
import software.amazon.awssdk.services.sqs.SqsAsyncClient
import software.amazon.awssdk.services.sqs.model.CreateQueueRequest
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest
import software.amazon.awssdk.services.sqs.model.DeleteQueueRequest
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest
import software.amazon.awssdk.services.sqs.model.SendMessageRequest
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest

object SqsExample extends IOApp {
  import com.github.rstradling.awsio.util.Transformations._
  val builder = SqsAsyncClient.builder().build
  val sqs: QueueOps[IO] = new QueueOpsAwsImpl[IO](builder)
  val message: MessageOps[IO] = new MessageOpsAwsImpl[IO](builder)

  def publish(): IO[Unit] = {
    val qName = "strad-test-queue"
    val createReq = CreateQueueRequest.builder.queueName(qName).build
    val urlRequest = GetQueueUrlRequest.builder.queueName(qName).build
    for {
      _ <- sqs.create(createReq)
      urlResp <- sqs.getUrl(urlRequest)
      deleteRequest = DeleteQueueRequest.builder.queueUrl(urlResp.queueUrl()).build()
      messageRequest = ReceiveMessageRequest.builder().queueUrl(urlResp.queueUrl()).build
      sendMessageRequest = SendMessageRequest.builder().queueUrl(urlResp.queueUrl())
        .messageBody("MyBody")
        .build
      _ <- message.send(sendMessageRequest)
      msg <- message.receive(messageRequest)
      x = msg.messages().asScala.head
      deleteMessageRequest = DeleteMessageRequest.builder.queueUrl(urlResp.queueUrl()).receiptHandle(x.receiptHandle()).build
      _ <- message.delete(deleteMessageRequest)
      _ = println(x)
      _ <- sqs.delete(deleteRequest)
    } yield ()
  }
  def run(args: List[String]): IO[ExitCode] = {
    publish().map(_ => ExitCode.Success)
  }
}
