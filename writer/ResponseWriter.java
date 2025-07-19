package writer;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import response.HttpResponse;

public class ResponseWriter {
    private ResponseWriter() {
    }

    public static void write(OutputStream out, HttpResponse response) throws IOException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8));
        System.out.println("RESPONSE:");
        String var10000 = response.getHttpVersion();
        String first = var10000 + " " + response.getStatus().getCode() + " " + response.getStatus().getReasonPhrase() + "\r\n";
        writer.write(first);
        System.out.println(first);
        Map<String, List<String>> headers = response.getResponseHeaders();

        for(Map.Entry<String, List<String>> entry : headers.entrySet()) {
            String var10001 = (String)entry.getKey();
            writer.write(var10001 + ": " + String.join(";", (Iterable)entry.getValue()) + "\r\n");
            PrintStream var8 = System.out;
            var10001 = (String)entry.getKey();
            var8.println(var10001 + ": " + String.join(";", (Iterable)entry.getValue()));
        }

        writer.write("\r\n");
        writer.flush();
        if (response.hasBody()) {
            byte[] arr = response.getHttpResponseBody().getRawContent();
            out.write(arr);
            out.flush();
        }

        writer.flush();
    }
}
