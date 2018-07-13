package com.github.rstradling.awsio.examples

import cats.effect.IO
import com.github.rstradling.awsio.sns.SnsTopicOpsAwsImpl
import com.github.rstradling.awsio.sns.SnsTopicOps
import software.amazon.awssdk.services.sns.SNSAsyncClient
import software.amazon.awssdk.services.sns.model.PublishRequest
import software.amazon.awssdk.services.sns.model.CreateTopicRequest
import scala.concurrent.duration._
import software.amazon.awssdk.services.sns.model.ListTopicsRequest
import software.amazon.awssdk.services.sns.model.DeleteTopicRequest

object SnsExample extends App {
  import com.strad.awsio.util.Transformations._
  val builder = SNSAsyncClient.builder().build
  val sns: SnsTopicOps[IO] = new SnsTopicOpsAwsImpl[IO](builder)

  def publish(): Unit = {
    val createReq = CreateTopicRequest.builder().name("strad-testing").build()
    val listTopicsReq = ListTopicsRequest.builder().build
    val res = for {
      createdResp <- sns.create(createReq)
      pubReq = PublishRequest.builder().message("Hello world").subject("Hello").targetArn(createdResp.topicArn()).build
      _ <- sns.publish(pubReq)
      items <- sns.list(listTopicsReq)
      _ = println(items)
      delTopReq = DeleteTopicRequest.builder().topicArn(createdResp.topicArn()).build()
      _ <- sns.delete(delTopReq)
    } yield ()
    res.unsafeRunTimed(10.seconds)
    ()
  }
  publish()
}
