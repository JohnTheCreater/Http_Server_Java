package test;

import server.Server;
import test.controllers.*;

import java.io.*;


public class Main {
    public static void main(String[] args) throws IOException {

       Server server = new Server(4040);
        server.get("/home", HomeController.printHome);

        server.get("/download/sample.txt", FileController.sendFile);

        server.post("/printBody", BodyController.printBody);

        server.get("/u/{id}/{name}", ParameterController.printParams);
        server.get("/printCookie", CookieController.printCookie);

        server.start();
    }

}


