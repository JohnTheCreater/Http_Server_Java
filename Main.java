
import java.io.File;
import java.io.IOException;
import java.util.List;
import request.RequestBody;
import request.RequestParams;
import response.HttpStatus;
import server.Server;

public class Main {
    public static void main(String[] args) throws IOException {
        Server server = new Server(4040);
        server.get("/home", (req, res) -> {
            String s = "<!DOCTYPE html>\n<html lang=\"en\">\n<head>\n    <meta charset=\"UTF-8\">\n    <title>My Custom Java server.Server</title>\n    <style>\n        body {\n            font-family: Arial, sans-serif;\n            background-color: #f6f6f6;\n            margin: 50px;\n            text-align: center;\n        }\n        h1 {\n            color: #4CAF50;\n        }\n        p {\n            font-size: 1.2rem;\n        }\n        a {\n            color: #007bff;\n            text-decoration: none;\n        }\n    </style>\n</head>\n<body>\n    <h1>Welcome to My Java server.Server!</h1>\n    <p>This page is served directly from a custom-built HTTP server in Java.</p>\n    <p><a href=\"/home\">Try /home route</a></p>\n</body>\n</html>\n";
            res.withBody(s);
            return res;
        });
        server.get("/download/sample.txt", (req, res) -> {
            File file = new File("src/files/sample.txt");

            try {
                res.withStatus(HttpStatus.OK).withHeader("Content-Disposition", List.of("attachment; filename=\"sample.txt\"")).withBody(file);
            } catch (RuntimeException var4) {
                res.withStatus(HttpStatus.NOT_FOUND).withBody("Cannot find The resource!");
            }

            return res;
        });
        server.post("/send", (req, res) -> {
            RequestBody body = req.getRequestBody();
            System.out.println(body.body());
            System.out.println(body.getString("name"));
            System.out.println(body.getMap("education").get("college"));
            System.out.println(body.getList("skills"));
            User user = new User("john", 400);
            res.withBody(user);
            return res;
        });
        server.get("/u/{id}/{name}", (req, res) -> {
            RequestParams params = req.getRequestParams();
            int id = params.getFirstInt("id");
            String name = params.getFirst("name");
            res.withBody("welcome user : " + name + " !      Your id :" + id);
            return res;
        });
        server.start();
    }
}
class User {
    public String name;
    public int age;

    public User(String name, int age) {
        this.name = name;
        this.age = age;
    }
}

