package writer;

import response.HttpResponse;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class ResponseWriter {

    private ResponseWriter(){}


    public static void write(OutputStream out, HttpResponse response) throws IOException {

        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8));
        System.out.println("RESPONSE:");
        String first = response.getHttpVersion() + " " +
                response.getStatus().getCode() + " " +
                response.getStatus().getReasonPhrase() + "\r\n";
        writer.write(first);
        System.out.println(first);
        Map<String, List<String>> headers = response.getResponseHeaders();




        for (Map.Entry<String, List<String>> entry : headers.entrySet()) {

                writer.write(entry.getKey() + ": " + String.join(";",entry.getValue()) + "\r\n");
            System.out.println(entry.getKey() + ": " + String.join(";",entry.getValue()) );

        }
        writer.write("\r\n");
        writer.flush();




        if(response.hasBody()) {
            byte[] arr = response.getHttpResponseBody().getRawContent();
            out.write(arr);
            out.flush();
        }

        writer.flush();

    }
}
