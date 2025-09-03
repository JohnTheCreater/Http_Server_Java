package test.controllers;

import server.RouteRunner;

public class HomeController {
    public static RouteRunner printHome = ((req, res) -> {
        String s = "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <title>My Custom Java server.Server</title>\n" +
                "    <style>\n" +
                "        body {\n" +
                "            font-family: Arial, sans-serif;\n" +
                "            background-color: #f6f6f6;\n" +
                "            margin: 50px;\n" +
                "            text-align: center;\n" +
                "        }\n" +
                "        h1 {\n" +
                "            color: #4CAF50;\n" +
                "        }\n" +
                "        p {\n" +
                "            font-size: 1.2rem;\n" +
                "        }\n" +
                "        a {\n" +
                "            color: #007bff;\n" +
                "            text-decoration: none;\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <h1>Welcome to My Java server.Server!</h1>\n" +
                "    <p>This page is served directly from a custom-built HTTP server in Java.</p>\n" +
                "    <p><a href=\"/home\">Try /home route</a></p>\n" +
                "</body>\n" +
                "</html>\n";
        res.withBody(s);

        return res;
    });
}
