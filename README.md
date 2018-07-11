# awsio
Amazon 2.0 SDK plus cats/cats-effect.  This is a library for working with cats-effect and AWS 2.0 SDK using a final tagless style.  
Please note that this is very much a WIP.  Philosophy wise we will not be trying to wrap the types like `ListBucketRequest`, `DeleteBucketResponse`
as those are core to aws and all that wrapping just takes extra time plus it can get out of date easily especially since AWS 2.0 SDK
is still in preview mode.

# Initial focus
Initial focus will be on SNS, SQS, and S3 and filling out the api as it relates to end user application surface areas (i.e. less about devops).
