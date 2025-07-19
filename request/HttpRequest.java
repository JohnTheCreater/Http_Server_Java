package request;

import java.net.URI;
import java.util.List;
import java.util.Map;


public class HttpRequest
{

    private final HttpMethod httpMethod;
    private final URI uri;
    private final String matchedRoute;
    private final Map<String, List<String>> requestHeader;
    private final RequestBody requestBody;
    private final RequestParams requestParams;


    private HttpRequest(Builder builder)
    {
        this.httpMethod = builder.httpMethod;
        this.requestHeader = builder.requestHeader;
        this.uri = builder.uri;
        this.requestBody = builder.requestBody;
        this.requestParams = builder.requestParams;
        this.matchedRoute = builder.matchedRoute;
    }


    public HttpMethod getHttpMethod()
    {
        return  this.httpMethod;
    }
    public URI getUri()
    {
        return this.uri;
    }
    public String getMatchedRoute()
    {
        return  this.matchedRoute;
    }
    public Map<String,List<String>> getRequestHeader()
    {
        return this.requestHeader;
    }
    public RequestBody getRequestBody()
    {
        if(!hasBody()) throw new IllegalStateException("This Request Does not have a Body!");
        return this.requestBody;
    }
    public RequestParams getRequestParams()
    {
        if(!hasParams()) throw new IllegalStateException("This Request Does not hold Parameter values!");
        return this.requestParams;
    }
    public boolean hasBody()
    {
        return this.requestBody != null;
    }
    public boolean hasParams()
    {
        return this.requestParams !=null ;
    }
    public static class Builder
    {
        private HttpMethod httpMethod;
        private URI uri;
        private Map<String, List<String>> requestHeader;
        private RequestBody requestBody;
        private RequestParams requestParams;
        private  String matchedRoute;

        public Builder()
        {
        }
        public Builder setHttpMethod(HttpMethod httpMethod)
        {
           this.httpMethod = httpMethod;
           return this;
        }
        public Builder setUri(URI uri)
        {
            this.uri = uri;
            return this;
        }
        public Builder setRequestHeader(Map<String, List<String>> requestHeader)
        {
            this.requestHeader = requestHeader;
            return this;
        }
        public Builder setBody(RequestBody body)
        {
            this.requestBody = body;
            return this;
        }
        public HttpRequest build()
        {
            return new HttpRequest(this);
        }

        public Builder setRequestParams(RequestParams requestParams) {
            this.requestParams = requestParams;
            return this;
        }

        public Builder setMatchedRoute(String matchedRoute) {
            this.matchedRoute = matchedRoute;
            return this;
        }
    }


}
