# Java AWS lambda distributed trace example app

This java app is an example of a Java app that runs in AWS lambda and is instrumented with New Relic

## Configuration

The Java part of the demo is based upon the [AWS Lambda sample app](https://github.com/awsdocs/aws-lambda-developer-guide/tree/master/sample-apps/java-basic) take a look at the instructions there for bootstrapping and deploying the app (effectively just running the helper scripts in the root of this repo). It has been adjusted to be instrumented by New Relic follwing the instructions found on the New Relic [documentation for Java instrumentation](https://docs.newrelic.com/docs/serverless-function-monitoring/aws-lambda-monitoring/get-started/enable-new-relic-monitoring-aws-lambda#java). 


Dont forget to set your New Relic account ID's to the template.yml

You will need to configure the URL's of the backends in src/main/java/example/Handler.java configure your backend URLs near line 60.
