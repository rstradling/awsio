package com.strad.awsio.examples

import cats.effect.IO
import com.strad.awsio.sqs.QueueOps
import com.strad.awsio.sqs.QueueOpsAwsImpl
import com.strad.awsio.sqs.MessageOps
import com.strad.awsio.sqs.MessageOpsAwsImpl
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
  import com.strad.awsio.util.Transformations._
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
      deleteRequest = DeleteQueueRequest.builder.queueUrl(urlResp.queueUrl()).build()
      messageRequest = ReceiveMessageRequest.builder().queueUrl(urlResp.queueUrl()).build
      sendMessageRequest = SendMessageRequest.builder().queueUrl(urlResp.queueUrl())
        .messageBody("MyBody")
        .build
      pubMsg <- message.send(sendMessageRequest)
      msg <- message.receive(messageRequest)
      x = msg.messages().asScala.head
      deleteMessageRequest = DeleteMessageRequest.builder.queueUrl(urlResp.queueUrl()).receiptHandle(x.receiptHandle()).build
      _ <- message.delete(deleteMessageRequest)
      _ = println(x)
      _ <- sqs.delete(deleteRequest)
    } yield ()
    res.unsafeRunTimed(10.seconds)
  }
  publish()

}
