# Node lambda distributed trace via SNS example

This project contains two nodejs apps that can run in AWS lambda. When the publisher is invoked it publishes a message to SNS which is consumerd by the 'consumer' app also running in AWS lambda. Put an API gateway in front of the publisher to call it via an http call. The apps are intented to be called as part of the distributed trace demo.

These apps user [serverless](https://github.com/serverless/serverless) to easily deploy to AWS lambda. See [New Relic documentation](https://docs.newrelic.com/docs/serverless-function-monitoring/aws-lambda-monitoring/get-started/enable-new-relic-monitoring-aws-lambda#serverless-plugin) for installation instructions. 

## Configuration

Be sure to set your New Relic account ID in the serverless.yml. 

Deploy with `sls deploy`

