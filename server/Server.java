package server;

import request.HttpMethod;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.*;

public class Server {
    private final ServerSocket serverSocket;
    private final Map<String,RouteRunner> routes;
    private final HttpHandler httpHandler;
    private final Executor threadPool;


    public Server(int port) throws IOException {

        serverSocket = new ServerSocket(port);
        routes = new LinkedHashMap<>();
        httpHandler = new HttpHandler(routes);
        threadPool = Executors.newFixedThreadPool(100);

    }
    public void start() throws IOException{
        while (!serverSocket.isClosed()) {
            try {
                Socket client = serverSocket.accept();
                handleConnection(client);
            } catch (SocketException e) {
                break; // stopped
            }
        }
    }

    private void handleConnection(Socket client) {

        threadPool.execute(() -> {
            try {
                httpHandler.handleConnection(
                        client.getInputStream(),
                        client.getOutputStream()
                );
            } catch ( Exception e ) {
                System.err.println("Connection error: " + e.getMessage());
            } finally {
                try { client.close(); } catch (IOException ignored) {}
            }
        });

    }

    public void get(String route,RouteRunner runner)
    {
        routes.put(HttpMethod.GET.name()+"::"+route,runner);
    }
    public void post(String route,RouteRunner runner)
    {
        routes.put(HttpMethod.POST.name()+"::"+route,runner);
    }
    public void put(String route,RouteRunner runner)
    {
        routes.put(HttpMethod.PUT.name()+"::"+route,runner);
    }
    public void patch(String route,RouteRunner runner)
    {
        routes.put(HttpMethod.PATCH.name()+"::"+route,runner);
    }
    public void delete(String route,RouteRunner runner)
    {
        routes.put(HttpMethod.DELETE.name()+"::"+route,runner);
    }



    public void stop() throws  InterruptedException {

        ExecutorService svc = (ExecutorService) threadPool;
        svc.shutdown();
        if ( !svc.awaitTermination(30, TimeUnit.SECONDS) ) {
            svc.shutdownNow();
        }
    }



}
