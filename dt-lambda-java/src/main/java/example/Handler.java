
package example;

import com.amazonaws.services.lambda.runtime.Context;
import com.newrelic.opentracing.aws.TracingRequestHandler;
import com.newrelic.opentracing.LambdaTracer;

import java.util.Map;
import java.util.HashMap;
import java.util.List;

import kong.unirest.GetRequest;
import kong.unirest.Header;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;

import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.propagation.Format;
import io.opentracing.propagation.TextMapInjectAdapter;
import io.opentracing.util.GlobalTracer;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;


/**
 * Tracing request handler that creates a span on every invocation of a Lambda.
 *
 * @param Map<String, Object> The Lambda Function input
 * @param String The Lambda Function output
 */
public class Handler implements TracingRequestHandler<Map<String, Object>, APIGatewayProxyResponseEvent> {
    static {
        // Register the New Relic OpenTracing LambdaTracer as the Global Tracer
        GlobalTracer.register(LambdaTracer.INSTANCE);
    }

    private final Tracer tracer = GlobalTracer.get();

    /**
     * Method that handles the Lambda function request.
     *
     * @param input The Lambda Function input
     * @param context The Lambda execution environment context object
     * @return String The Lambda Function output
     */
    @Override
    public APIGatewayProxyResponseEvent doHandleRequest(Map<String, Object> input, Context context) {
        HttpResponse<String> response;

        // Log Lambda function input details received from API Gateway to the Cloudwatch logs
        if (input != null && !input.isEmpty()) {
            System.out.println("Lambda function input: " + input);
        } else {
            System.out.println("Lambda function did not receive any input");
        }

        //Configure these requests as per your setup. You can just make one request or multiple requests to see how the trace changes. 
        //We only display the response from the final request
        response = MyGETRequest("Node1","https://your-node-lambdaapp-here.execute-api.eu-west-1.amazonaws.com/testing/node-lambda?name=FromJavaLambda");
        response = MyGETRequest("Java1","https://backend-dns-here.herokuapp.com");
        //if(1==1) {throw new java.lang.Error("this is very bad");} //example error, uncomment this t osee what an error looks like
        response = MyGETRequest("Java2","https://backend-dns-here.herokuapp.com");
         
        return new APIGatewayProxyResponseEvent()
                .withBody(response.getBody())
                .withStatusCode(response.getStatus())
                .withHeaders(getResponseHeaderMap(response));
        
    }

    private HttpResponse<String> MyGETRequest(String getName, String getUrl) {
        HttpResponse<String> response;
        final Map<String, String> distributedTracingHeaders = new HashMap<>();

        try (Scope scope = GlobalTracer.get().buildSpan("externalAPICall-"+getName).startActive(true)) {
 /*
             * It is important to get the last active span before calling tracer.inject below so that its context gets
             * propagated in the "newrelic" DT headers and the DT UI can do proper span parenting.
             */
            final Span activeSpan = tracer.activeSpan();

            /*
             * This call to inject will populate the distributedTracingHeaders map with "newrelic" DT headers of the form:
             * "newrelic" -> "eyJkIjp7ImFjIjoiMjIxMjg2NCIsInByIjoxLjkyMjkxNTksInR4IjoiNjNlOTIz..."
             * The DT headers can then easily be attached as headers to an outgoing request.
             */
            tracer.inject(activeSpan.context(), Format.Builtin.HTTP_HEADERS, new TextMapInjectAdapter(distributedTracingHeaders));

            String url=getUrl;
            GetRequest request = Unirest.get(url);

            // Attach the "newrelic" DT headers to the outgoing request if they exist
            if (distributedTracingHeaders.containsKey("newrelic")) {
                System.out.println("New relic header detected");
                request.header("newrelic", distributedTracingHeaders.get("newrelic"));
                scope.span().setTag("newrelicHeaders", true); //indicate on the span the newrelic header was present
            } else {
                System.out.println("New relic header NOT detected");
            }

            // Request is fired off when asString() is called
            response = request.header("accept", "application/json").asString();

            //enrich the span attributes with some of the info
            scope.span().setTag("externalUrl", url);
            scope.span().setTag("responseMessage", response.getStatusText());
            scope.span().setTag("responseCode", response.getStatus());     

            return response;
            
        }

     
  }

   /**
     * Converts the List of Header objects from the HttpResponse into a Map
     *
     * @param response HttpResponse
     * @return Map of headers
     */
    private Map<String, String> getResponseHeaderMap(HttpResponse response) {
        final List<Header> headerList = response.getHeaders().all();
        final Map<String, String> headerMap = new HashMap<>();
        headerList.forEach((header) -> headerMap.put(header.getName(), header.getValue()));
        return headerMap;
    }


}