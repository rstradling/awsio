package com.github.rstradling.awsio.sns

import cats.effect.Async
import cats.~>
import java.util.concurrent.CompletableFuture
import software.amazon.awssdk.services.sns.SnsAsyncClient
import software.amazon.awssdk.services.sns.model._

class SnsTopicOpsAwsImpl[F[_]](client: SnsAsyncClient)(implicit F: Async[F], transform: CompletableFuture ~> F) extends SnsTopicOps[F] {
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
  def delete(deleteTopicRequest: DeleteTopicRequest): F[DeleteTopicResponse] = {
    transform(client.deleteTopic(deleteTopicRequest))
  }
}

