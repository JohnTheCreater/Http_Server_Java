package test.controllers;

import request.RequestBody;
import server.RouteRunner;

public class BodyController {
    public  static RouteRunner printBody = (req, res) -> {
        RequestBody body = req.getRequestBody();
        System.out.println(body.body());

        User user = new User("John Joshua",400,"Information Technology");

        res.withBody(user);
        return res;
    };

    static class User
    {
        public String name;
        public int age;
        public String course;
        public User(String name,int age,String course)
        {
            this.name = name;
            this.age = age;
            this.course = course;
        }

    }
}
