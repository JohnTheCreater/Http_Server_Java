package server;

import request.HttpRequest;
import response.HttpResponse;

public interface RouteRunner {

    HttpResponse.Builder run(HttpRequest req, HttpResponse.Builder res);

}
