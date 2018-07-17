package com.github.rstradling.awsio.sns

import software.amazon.awssdk.services.sns.model._

/**
  * Final tagless style for SNS topic operations
  * @tparam F - The effect type to use like Monix.Task or cats.effect.IO
  */
trait SnsTopicOps[F[_]] {
  def create(createTopicRequest: CreateTopicRequest): F[CreateTopicResponse]
  def publish(publishRequest: PublishRequest): F[PublishResponse]
  def list(listTopicsRequest: ListTopicsRequest): F[ListTopicsResponse]
  def subscribe(subscribeRequest: SubscribeRequest): F[SubscribeResponse]
  def delete(deleteTopicRequest: DeleteTopicRequest): F[DeleteTopicResponse]
}
