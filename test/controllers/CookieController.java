package test.controllers;

import response.HttpStatus;
import server.RouteRunner;

public class CookieController {
    public static RouteRunner printCookie = (req, res) -> {
      String token = req.getCookie().get("token");
        System.out.println("received token : " +token);
        res.withStatus(HttpStatus.OK);
        return res;
    };
}
