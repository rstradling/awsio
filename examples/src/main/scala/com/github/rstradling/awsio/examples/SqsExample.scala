package com.github.rstradling.awsio.examples

import cats.effect.IO
import cats.implicits._
import com.github.rstradling.awsio.sqs.QueueOps
import com.github.rstradling.awsio.sqs.MessageOps
import com.github.rstradling.awsio.sqs.QueueOpsAwsImpl
import com.github.rstradling.awsio.sqs.MessageOpsAwsImpl
import software.amazon.awssdk.services.sqs.SQSAsyncClient
import software.amazon.awssdk.services.sqs.model.CreateQueueRequest
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest
import software.amazon.awssdk.services.sqs.model.DeleteQueueRequest
import scala.collection.JavaConverters._
import scala.concurrent.duration._
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest
import software.amazon.awssdk.services.sqs.model.SendMessageRequest
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest

object SqsExample extends App {
  import com.github.rstradling.awsio.util.Transformations._
  val builder = SQSAsyncClient.builder().build
  val sqs: QueueOps[IO] = new QueueOpsAwsImpl[IO](builder)
  val message: MessageOps[IO] = new MessageOpsAwsImpl[IO](builder)

  def publish(): Unit = {
    val qName = "strad-test-queue"
    val createReq = CreateQueueRequest.builder.queueName(qName).build
    val urlRequest = GetQueueUrlRequest.builder.queueName(qName).build
    val res = for {
      createdResp <- sqs.create(createReq)
      urlResp <- sqs.getUrl(urlRequest)
      deleteRequest = DeleteQueueRequest.builder
        .queueUrl(urlResp.queueUrl())
        .build()
      messageRequest = ReceiveMessageRequest
        .builder()
        .maxNumberOfMessages(1)
        .queueUrl(urlResp.queueUrl())
        .build
      sendMessageRequest = SendMessageRequest
        .builder()
        .queueUrl(urlResp.queueUrl())
        .messageBody("MyBody")
        .build
      pubMsg <- message.send(sendMessageRequest)
      msg <- message.receive(messageRequest)
      messages = msg.messages().asScala
      deleteMessageRequest = messages.map(
        x =>
          DeleteMessageRequest.builder
            .queueUrl(urlResp.queueUrl())
            .receiptHandle(x.receiptHandle())
            .build)
      _ <- deleteMessageRequest.toList.traverse(message.delete)
      _ = println(messages)
      _ <- sqs.delete(deleteRequest)
    } yield ()
    val _ = res.unsafeRunTimed(10.seconds)
    ()
  }
  publish()

}
