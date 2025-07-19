package response;

import content.ContentType;
import util.HttpEncoder;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class HttpResponse {

    private final String httpVersion;
    private final HttpStatus status;
    private final Map<String, List<String>> responseHeaders;
    private final HttpResponseBody httpResponseBody ;


    private HttpResponse(Builder builder)
    {
        this.httpVersion = builder.httpVersion;
        this.status = builder.status;
        this.responseHeaders = builder.responseHeaders;
        this.httpResponseBody = builder.httpResponseBody;
    }


    public boolean hasBody()
    {
        return !this.httpResponseBody.isEmpty();
    }

    public HttpResponseBody getHttpResponseBody()
    {
        return this.httpResponseBody;
    }

    public String getHttpVersion() {
        return httpVersion;
    }

    public HttpStatus getStatus() {

        return status;
    }

    public Map<String, List<String>> getResponseHeaders() {
        return responseHeaders;
    }

    public String getBodyAsString()
    {
        return this.httpResponseBody.toString(StandardCharsets.UTF_8);
    }



    public static class Builder
    {
        private String httpVersion = "HTTP/1.1";
        private HttpStatus status = HttpStatus.OK;
        private  final Map<String, List<String>> responseHeaders;
        private final HttpResponseBody httpResponseBody ;

        public Builder()
        {
            this.responseHeaders = new HashMap<>();
            this.httpResponseBody = new HttpResponseBody();
            initializeHeader();
        }

        private void initializeHeader() {

            responseHeaders.put("Date", List.of(ZonedDateTime
                    .now(ZoneOffset.UTC)
                    .format(DateTimeFormatter.RFC_1123_DATE_TIME)));
            responseHeaders.put("server.Server", List.of("MyCustomServer/1.0"));
            responseHeaders.put("Connection", List.of("close"));

        }

        public Builder setHttpVersion(String version)
        {
            this.httpVersion = version;
            return this;
        }
        public Builder withStatus(HttpStatus status)
        {
            this.status = status;
            return this;
        }
        public Builder withHeader(String name,List<String> value)
        {
            this.responseHeaders.put(name,value);

            return this;
        }

        public Builder withBody(Object object)
        {
            byte[] rawContent;

            switch (object) {
                case String s -> {
                    rawContent = s.getBytes(StandardCharsets.UTF_8);
                    this.httpResponseBody.setContentType(detectBodyType(s));

                }
                case byte[] binaryContent -> {
                    rawContent = binaryContent;
                    this.httpResponseBody.setContentType(ContentType.OCTET_STREAM);
                }
                case InputStream inputStream -> {
                    try {
                        rawContent = toByteArray(inputStream);
                        this.httpResponseBody.setContentType(ContentType.OCTET_STREAM);

                    } catch (IOException e) {
                        resetUnsafeHeaders();
                        throw new RuntimeException("Failed to Read InputStream", e);
                    }
                }
                case File file -> {
                    try {
                        rawContent = Files.readAllBytes(file.toPath());
                        String mimeType = Files.probeContentType(file.toPath());
                        ContentType contentType = ContentType.fromMimeType(mimeType.split(";")[0].trim());
                        this.httpResponseBody.setContentType(contentType);
                    }
                    catch (IOException e)
                    {
                        resetUnsafeHeaders();
                        throw new RuntimeException("Failed to Read The File");
                    }
                }
                case null, default -> {

                    String rawBodyContent = HttpEncoder.getJSON(object,new HashMap<>());
                    rawContent = rawBodyContent.getBytes(StandardCharsets.UTF_8);
                    this.httpResponseBody.setContentType(ContentType.JSON);
                }
            }

            this.httpResponseBody.setRawContent(rawContent);


            return this;

        }

        private void resetUnsafeHeaders() {
            this.responseHeaders.remove("Content-Disposition");
            this.responseHeaders.remove("Content-Type");
            this.responseHeaders.remove("Content-Encoding");
            this.responseHeaders.remove("Content-Length");
            this.responseHeaders.remove("Transfer-Encoding");
        }


        private byte[] toByteArray(InputStream inputStream) throws IOException {
            return inputStream.readAllBytes();

        }

        public static ContentType detectBodyType(String stringData) {

            if (stringData == null || stringData.isEmpty()) return ContentType.EMPTY;

            if (stringData.startsWith("<?xml") && stringData.endsWith("?>")) {
                return ContentType.XML;
            }

            return ContentType.HTML;

        }

        public HttpResponse build()
        {
            this.responseHeaders.put("Content-Length",List.of((httpResponseBody.getRawContent().length)+""));
            if(!responseHeaders.containsKey("Content-Type"))
                this.responseHeaders.put("Content-Type",List.of( httpResponseBody.getContentType().value() ));


            return new HttpResponse(this);
        }
    }


}
