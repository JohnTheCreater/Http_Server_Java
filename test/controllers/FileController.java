package test.controllers;

import response.HttpStatus;
import server.RouteRunner;

import java.io.File;
import java.util.List;

public class FileController {

    public static RouteRunner sendFile = (req,res) -> {
        File file = new File("src/files/sample.txt");

        try {
            res.withStatus(HttpStatus.OK)
                    .withHeader("Content-Disposition", List.of("attachment; filename=\"sample.txt\""))
                    .withBody(file);

        }
        catch (RuntimeException e)
        {
            res.withStatus(HttpStatus.NOT_FOUND).withBody("Cannot find The resource!");
        }
        return res;
    };
}
