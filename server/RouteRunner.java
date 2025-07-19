package server;

import request.HttpRequest;
import response.HttpResponse;

public interface RouteRunner {
    HttpResponse.Builder run(HttpRequest var1, HttpResponse.Builder var2);
}