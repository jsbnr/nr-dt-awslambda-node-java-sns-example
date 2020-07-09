'use strict';
var newrelic = require('newrelic');

module.exports.hello = async event => {
  let transaction = newrelic.getTransaction()
  newrelic.startSegment("recvMessage",false,()=>{  
    var message = event.Records[0].Sns.Message;
    console.log('Message received from SNS:', message);
    console.log('Record data:', JSON.stringify(event.Records[0]));
  
    if(event.Records[0].Sns.MessageAttributes && event.Records[0].Sns.MessageAttributes.newrelic) {
      console.log("trace context received")
      transaction.acceptDistributedTraceHeaders("sns",JSON.parse(event.Records[0].Sns.MessageAttributes.newrelic.Value)) //accept the trace context
    } else {
      console.log("No trace context received in message attributes")
    }

    return {
      statusCode: 200,
      body: JSON.stringify(
        {
          message: `SNS message: ${message}`,
          input: event,
        },
        null,
        2
      ),
    };
    })
};
