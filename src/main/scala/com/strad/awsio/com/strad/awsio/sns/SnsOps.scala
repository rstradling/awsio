package com.strad.awsio.com.strad.awsio.sns

import cats.~>
import cats.effect.Async
import java.util.concurrent.CompletableFuture
import software.amazon.awssdk.services.sns.SNSAsyncClient
import software.amazon.awssdk.services.sns.model.CreateTopicRequest
import software.amazon.awssdk.services.sns.model.CreateTopicResponse
import software.amazon.awssdk.services.sns.model.PublishRequest
import software.amazon.awssdk.services.sns.model.PublishResponse
import software.amazon.awssdk.services.sns.model.ListTopicsRequest
import software.amazon.awssdk.services.sns.model.ListTopicsResponse
import software.amazon.awssdk.services.sns.model.SubscribeRequest
import software.amazon.awssdk.services.sns.model.SubscribeResponse

trait SnsTopicOps[F[_]] {
  def create(createTopicRequest: CreateTopicRequest): F[CreateTopicResponse]
  def publish(publishRequest: PublishRequest): F[PublishResponse]
  def list(listTopicsRequest: ListTopicsRequest): F[ListTopicsResponse]
  def subscribe(subscribeRequest: SubscribeRequest): F[SubscribeResponse]
}

class SnsTopicOpsImpl[F[_]](client: SNSAsyncClient)(implicit f: Async[F], transform: CompletableFuture ~> F) extends SnsTopicOps[F] {
  def create(createTopicRequest: CreateTopicRequest): F[CreateTopicResponse] = {
    transform(client.createTopic(createTopicRequest))
  }
  def publish(publishRequest: PublishRequest): F[PublishResponse] = {
    transform(client.publish(publishRequest))
  }
  def list(listTopicsRequest: ListTopicsRequest): F[ListTopicsResponse] = {
    transform(client.listTopics(listTopicsRequest))
  }
  def subscribe(subscribeRequest: SubscribeRequest): F[SubscribeResponse] = {
    transform(client.subscribe(subscribeRequest))
  }
}

