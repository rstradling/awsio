package com.github.rstradling.awsio.examples

import cats.effect.ExitCode
import cats.effect.IO
import cats.effect.IOApp
import com.github.rstradling.awsio.sns.SnsTopicOpsAwsImpl
import com.github.rstradling.awsio.sns.SnsTopicOps
import scala.concurrent.duration._
import software.amazon.awssdk.services.sns.SnsAsyncClient
import software.amazon.awssdk.services.sns.model.PublishRequest
import software.amazon.awssdk.services.sns.model.CreateTopicRequest
import software.amazon.awssdk.services.sns.model.ListTopicsRequest
import software.amazon.awssdk.services.sns.model.DeleteTopicRequest

object SnsExample extends IOApp {
  import com.github.rstradling.awsio.util.Transformations._
  val builder = SnsAsyncClient.builder().build
  val sns: SnsTopicOps[IO] = new SnsTopicOpsAwsImpl[IO](builder)

  def publish(): IO[Unit]  = {
    val createReq = CreateTopicRequest.builder().name("strad-testing").build()
    val listTopicsReq = ListTopicsRequest.builder().build
    for {
      createdResp <- sns.create(createReq)
      pubReq = PublishRequest.builder().message("Hello world").subject("Hello").targetArn(createdResp.topicArn()).build
      _ <- sns.publish(pubReq)
      items <- sns.list(listTopicsReq)
      _ = println(items)
      delTopReq = DeleteTopicRequest.builder().topicArn(createdResp.topicArn()).build()
      _ <- sns.delete(delTopReq)
    } yield ()
  }
  def run(args: List[String]): IO[ExitCode] = {
    publish().map(_ => ExitCode.Success)
  }
}
