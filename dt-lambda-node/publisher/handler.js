'use strict';
var AWS = require('aws-sdk');
var newrelic = require('newrelic');


module.exports.hello = async (event) => {
  function sendSNSMessage() {

    return new Promise((resolve, reject) => {
        
        let tx = newrelic.getTransaction()
        let traceContext={}
        tx.insertDistributedTraceHeaders(traceContext) //populate the tracecontext object with the trace context headers

        newrelic.startSegment("sendMessage",false,async ()=>{
          
          let msg="Test message:"
          if (event.queryStringParameters && event.queryStringParameters.name) {
            msg += event.queryStringParameters.name;
          }
        var params = {
          Message: msg, /* required */
          TopicArn: 'arn:aws:sns:eu-west-1:037844839922:exampleTopic',
          MessageAttributes: {
            'newrelic': { DataType: 'String', StringValue: JSON.stringify(traceContext) }, //add trace context via message attributes
          },
        };
        // Create promise and SNS service object
          console.log('Sending sns message')
          var publishTextPromise = new AWS.SNS({apiVersion: '2010-03-31'}).publish(params).promise();
          publishTextPromise.then(
            function(data) {
              console.log(`Message ${params.Message} send sent to the topic ${params.TopicArn}`);
              console.log("MessageID is " + data.MessageId);
             // tx.end(()=>{resolve("sent")})
             resolve("sent")
             
            }).catch(
              function(err) {
              console.error(err, err.stack);
              reject("error")
              //tx.end(()=>{ reject("error")})
            });

        })

    });
      
  }

  await sendSNSMessage()

//some random functionality for the response....

    let name = "you";
    let city = 'World';
    let time = 'day';
    let day = '';
    let responseCode = 200;
    console.log("request: " + JSON.stringify(event));
    
    if (event.queryStringParameters && event.queryStringParameters.name) {
        console.log("Received name: " + event.queryStringParameters.name);
        name = event.queryStringParameters.name;
    }
    
    if (event.queryStringParameters && event.queryStringParameters.city) {
        console.log("Received city: " + event.queryStringParameters.city);
        city = event.queryStringParameters.city;
    }
    
    if (event.headers && event.headers['day']) {
        console.log("Received day: " + event.headers.day);
        day = event.headers.day;
    }
    
    if (event.body) {
        let body = JSON.parse(event.body)
        if (body.time) 
            time = body.time;
    }
 
    let greeting = `Good ${time}, ${name} of ${city}.`;
    if (day) greeting += ` Happy ${day}!`;

    let responseBody = {
        message: greeting,
        input: event
    };
    
    // The output from a Lambda proxy integration must be 
    // in the following JSON object. The 'headers' property 
    // is for custom response headers in addition to standard 
    // ones. The 'body' property  must be a JSON string. For 
    // base64-encoded payload, you must also set the 'isBase64Encoded'
    // property to 'true'.
    let response = {
        statusCode: responseCode,
        headers: {
            "x-custom-header" : "my custom header value"
        },
        body: JSON.stringify(responseBody)
    };
    console.log("response: " + JSON.stringify(response))
    return response;
};