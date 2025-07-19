package server;

import request.HttpRequest;

import response.HttpResponse;
import response.HttpStatus;
import util.HttpDecoder;
import writer.ResponseWriter;

import java.io.*;

import java.net.URISyntaxException;
import java.util.*;

public class HttpHandler {

    private final Map<String,RouteRunner> routes;


    public HttpHandler(Map<String,RouteRunner> routes)
    {
        this.routes = routes;
    }


    public void handleConnection(final InputStream inputStream, final OutputStream outputStream) throws URISyntaxException, IOException {

        try {
            List<String> requestInfo = readRequestInfo(inputStream);
            Optional<HttpRequest> httpRequest = HttpDecoder.decode(requestInfo, routes.keySet());

            if (httpRequest.isEmpty())
                handleInValidRequest(outputStream);
            else
                handleValidRequest(outputStream, httpRequest.get());
        }
        catch (Exception e)
        {

            handleInValidRequest(outputStream);
            throw e;
        }

    }

    public void handleValidRequest(OutputStream out, HttpRequest request) throws IOException {

        String routeKey = request.getMatchedRoute();

            if (routes.containsKey(routeKey)) {

                HttpResponse.Builder responseBuilder = routes.get(routeKey).run(request, new HttpResponse.Builder());
                ResponseWriter.write(out, responseBuilder.build());
            }


    }

    public void handleInValidRequest(OutputStream out) throws IOException {

        HttpResponse.Builder resBuilder = new HttpResponse.Builder()
                .withStatus(HttpStatus.NOT_FOUND).withBody(pageNotFoundHtml);

        HttpResponse response = resBuilder.build();
        ResponseWriter.write(out,response);
    }

    private List<String> readRequestInfo(InputStream inputStream) throws IOException {

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        boolean hasBody = false;
        int contentLength = 0;

        List<String> message = new ArrayList<>();
        while ((line = reader.readLine() ) != null && !line.isEmpty()) {
            message.add(line);
            if(line.startsWith("Content-Length"))
            {
                hasBody = true;
                String[] parts = line.split(":");
                contentLength = Integer.parseInt(parts[1].trim());
            }


        }


        if (hasBody ){

            char[] bodyChars = new char[contentLength];
            reader.read(bodyChars, 0, contentLength);
            String requestBody = new String(bodyChars);
            message.add("Body:" + requestBody);
        }




        return message;

    }

    private final String pageNotFoundHtml = """
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
  <title>404 - Page Not Found</title>
  <style>
    body {
      background-color: #f8f9fa;
      font-family: Arial, sans-serif;
      margin: 0;
      display: flex;
      flex-direction: column;
      justify-content: center;
      align-items: center;
      height: 100vh;
      color: #333;
      text-align: center;
    }

    h1 {
      font-size: 5rem;
      margin-bottom: 0.5rem;
    }

    p {
      font-size: 1.5rem;
      margin-bottom: 2rem;
    }

    a {
      text-decoration: none;
      color: white;
      background-color: #007bff;
      padding: 0.75rem 1.5rem;
      border-radius: 0.3rem;
      transition: background-color 0.3s;
    }

    a:hover {
      background-color: #0056b3;
    }
  </style>
</head>
<body>
  <h1>404</h1>
  <p>Oops! The page you're looking for doesn't exist.</p>
  <a href="/home">Go Home</a>
</body>
</html>
""";
}
