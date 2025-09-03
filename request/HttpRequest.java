package request;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class HttpRequest
{

    private final HttpMethod httpMethod;
    private final URI uri;
    private final String matchedRoute;
    private final Map<String, List<String>> requestHeader;
    private final RequestBody requestBody;
    private final PathParams pathParams;
    private final QueryParams queryParams;
    private final Cookie cookie;


    private HttpRequest(Builder builder)
    {
        this.httpMethod = builder.httpMethod;
        this.requestHeader = builder.requestHeader;
        this.uri = builder.uri;
        this.requestBody = builder.requestBody;
        this.pathParams = builder.pathParams;
        this.queryParams = builder.queryParams;
        this.matchedRoute = builder.matchedRoute;
        this.cookie = builder.cookie;
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
    public PathParams getPathParams()
    {
        return this.pathParams;
    }

    public QueryParams getQueryParams()
    {
        return this.queryParams;
    }


    public boolean hasBody()
    {
        return this.requestBody != null;
    }

    public Cookie getCookie()
    {
        return this.cookie;
    }

                public static class Builder
                {
                    private HttpMethod httpMethod;
                    private URI uri;
                    private Map<String, List<String>> requestHeader;
                    private RequestBody requestBody;
                    private PathParams pathParams = new PathParams(new HashMap<>());
                    private QueryParams queryParams = new QueryParams((new HashMap<>()));
                    private String matchedRoute;
                    private Cookie cookie = new Cookie();

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

                    public Builder setCookie(Cookie cookie)
                    {
                        this.cookie = cookie;
                        return this;
                    }

                   public Builder setQueryParams(QueryParams queryParams)
                   {
                       this.queryParams = queryParams;
                       return this;
                   }
                   public Builder setPathParams(PathParams pathParams)
                   {
                       this.pathParams = pathParams;
                       return this;
                   }

                    public Builder setMatchedRoute(String matchedRoute) {
                        this.matchedRoute = matchedRoute;
                        return this;
                    }

                    public HttpRequest build()
                    {
                        return new HttpRequest(this);
                    }

                }


}
