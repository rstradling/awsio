package com.strad.awsio.sns

import software.amazon.awssdk.services.sns.model._

trait SnsTopicOps[F[_]] {
  def create(createTopicRequest: CreateTopicRequest): F[CreateTopicResponse]
  def publish(publishRequest: PublishRequest): F[PublishResponse]
  def list(listTopicsRequest: ListTopicsRequest): F[ListTopicsResponse]
  def subscribe(subscribeRequest: SubscribeRequest): F[SubscribeResponse]
  def delete(deleteTopicRequest: DeleteTopicRequest): F[DeleteTopicResponse]
}
