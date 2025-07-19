package server;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import request.HttpRequest;
import response.HttpResponse;
import response.HttpStatus;
import util.HttpDecoder;
import writer.ResponseWriter;

public class HttpHandler {
    private final Map<String, RouteRunner> routes;
    private final String pageNotFoundHtml = "<!DOCTYPE html>\n<html lang=\"en\">\n<head>\n  <meta charset=\"UTF-8\" />\n  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"/>\n  <title>404 - Page Not Found</title>\n  <style>\n    body {\n      background-color: #f8f9fa;\n      font-family: Arial, sans-serif;\n      margin: 0;\n      display: flex;\n      flex-direction: column;\n      justify-content: center;\n      align-items: center;\n      height: 100vh;\n      color: #333;\n      text-align: center;\n    }\n\n    h1 {\n      font-size: 5rem;\n      margin-bottom: 0.5rem;\n    }\n\n    p {\n      font-size: 1.5rem;\n      margin-bottom: 2rem;\n    }\n\n    a {\n      text-decoration: none;\n      color: white;\n      background-color: #007bff;\n      padding: 0.75rem 1.5rem;\n      border-radius: 0.3rem;\n      transition: background-color 0.3s;\n    }\n\n    a:hover {\n      background-color: #0056b3;\n    }\n  </style>\n</head>\n<body>\n  <h1>404</h1>\n  <p>Oops! The page you're looking for doesn't exist.</p>\n  <a href=\"/home\">Go Home</a>\n</body>\n</html>\n";

    public HttpHandler(Map<String, RouteRunner> routes) {
        this.routes = routes;
    }

    public void handleConnection(InputStream inputStream, OutputStream outputStream) throws URISyntaxException, IOException {
        try {
            List<String> requestInfo = this.readRequestInfo(inputStream);
            Optional<HttpRequest> httpRequest = HttpDecoder.decode(requestInfo, this.routes.keySet());
            if (httpRequest.isEmpty()) {
                this.handleInValidRequest(outputStream);
            } else {
                this.handleValidRequest(outputStream, (HttpRequest)httpRequest.get());
            }

        } catch (Exception e) {
            this.handleInValidRequest(outputStream);
            throw e;
        }
    }

    public void handleValidRequest(OutputStream out, HttpRequest request) throws IOException {
        String routeKey = request.getMatchedRoute();
        if (this.routes.containsKey(routeKey)) {
            HttpResponse.Builder responseBuilder = ((RouteRunner)this.routes.get(routeKey)).run(request, new HttpResponse.Builder());
            ResponseWriter.write(out, responseBuilder.build());
        }

    }

    public void handleInValidRequest(OutputStream out) throws IOException {
        HttpResponse.Builder resBuilder = (new HttpResponse.Builder()).withStatus(HttpStatus.NOT_FOUND).withBody("<!DOCTYPE html>\n<html lang=\"en\">\n<head>\n  <meta charset=\"UTF-8\" />\n  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"/>\n  <title>404 - Page Not Found</title>\n  <style>\n    body {\n      background-color: #f8f9fa;\n      font-family: Arial, sans-serif;\n      margin: 0;\n      display: flex;\n      flex-direction: column;\n      justify-content: center;\n      align-items: center;\n      height: 100vh;\n      color: #333;\n      text-align: center;\n    }\n\n    h1 {\n      font-size: 5rem;\n      margin-bottom: 0.5rem;\n    }\n\n    p {\n      font-size: 1.5rem;\n      margin-bottom: 2rem;\n    }\n\n    a {\n      text-decoration: none;\n      color: white;\n      background-color: #007bff;\n      padding: 0.75rem 1.5rem;\n      border-radius: 0.3rem;\n      transition: background-color 0.3s;\n    }\n\n    a:hover {\n      background-color: #0056b3;\n    }\n  </style>\n</head>\n<body>\n  <h1>404</h1>\n  <p>Oops! The page you're looking for doesn't exist.</p>\n  <a href=\"/home\">Go Home</a>\n</body>\n</html>\n");
        HttpResponse response = resBuilder.build();
        ResponseWriter.write(out, response);
    }

    private List<String> readRequestInfo(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        boolean hasBody = false;
        int contentLength = 0;
        List<String> message = new ArrayList();

        String line;
        while((line = reader.readLine()) != null && !line.isEmpty()) {
            message.add(line);
            if (line.startsWith("Content-Length")) {
                hasBody = true;
                String[] parts = line.split(":");
                contentLength = Integer.parseInt(parts[1].trim());
            }
        }

        if (hasBody) {
            char[] bodyChars = new char[contentLength];
            reader.read(bodyChars, 0, contentLength);
            String requestBody = new String(bodyChars);
            message.add("Body:" + requestBody);
        }

        return message;
    }
}
