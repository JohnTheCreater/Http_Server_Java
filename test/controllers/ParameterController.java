package test.controllers;

import request.PathParams;
import server.RouteRunner;

public class ParameterController {

    public static RouteRunner printParams = ((req, res) -> {
        PathParams params = req.getPathParams();
        int id = params.getInt("id");
        String name = params.get("name");
        res.withBody("welcome user : "+name +" !      Your id :"+id);
        return res;
    });
}
