package com.strad.awsio.examples

import software.amazon.awssdk.services.sns.SNSAsyncClient

class S3Example extends App {
  val builder = SNSAsyncClient.create()
}
