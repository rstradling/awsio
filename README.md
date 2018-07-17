[![Build Status](https://travis-ci.org/rstradling/awsio.svg?branch=master)](https://travis-ci.org/rstradling/awsio) [![Gitter](https://img.shields.io/gitter/room/rstradling/awsio.svg)](https://gitter.im/rstradling/awsio) [![Latest version](https://index.scala-lang.org/rstradling/awsio/latest.svg?color=orange)](https://index.scala-lang.org/rstradling/awsio/awsio) [![Coverage Status](https://codecov.io/gh/rstradling/awsio/coverage.svg?branch=master)](https://codecov.io/gh/rstradling/awsio?branch=master)
# awsio
Amazon 2.0 SDK plus cats/cats-effect.  This is a library for working with cats-effect and AWS 2.0 SDK using a final tagless style.
Please note that this is very much a WIP.  Philosophy wise we will not be trying to wrap the types like `ListBucketRequest`, `DeleteBucketResponse`
as those are core to aws and all that wrapping just takes extra time plus it can get out of date easily especially since AWS 2.0 SDK
is still in preview mode.  Please note a good amount of the dependencies are newer (AWS 2.0 SDK, Monix RC, fs2)

# Initial focus
Initial focus will be on SNS, SQS, and S3 and filling out the api as it relates to end user application surface areas (i.e. less about devops).

# Modules
* awsio-s3 - Implementation of S3 operations on buckets and objects.  The object one needs lots of love and is not at 
all fit for production use.  The bucket ones seems to work fine
* awsio-sns - Implementation of SNS operations on topics.  It seems to work fine.
* awsio-sqs - Implementation of SQS operations on queues.  It seems to work fine but may still require a bit of tweaking API wise.
* awsio-sqs-fs2 - Implementation of a receiveLoop and AckProcessor for SQS using fs2 streams.
* awsio-sqs-monix - Implementation of a receiveLoop and AckProcessor for SQS using monix iterant
