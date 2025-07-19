package server;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import request.HttpMethod;

public class Server {
    private final ServerSocket serverSocket;
    private final Map<String, RouteRunner> routes;
    private final HttpHandler httpHandler;
    private final Executor threadPool;

    public Server(int port) throws IOException {
        this.serverSocket = new ServerSocket(port);
        this.routes = new HashMap();
        this.httpHandler = new HttpHandler(this.routes);
        this.threadPool = Executors.newFixedThreadPool(100);
    }

    public void start() throws IOException {
        while(true) {
            if (!this.serverSocket.isClosed()) {
                try {
                    Socket client = this.serverSocket.accept();
                    this.handleConnection(client);
                    continue;
                } catch (SocketException var2) {
                }
            }

            return;
        }
    }

    private void handleConnection(Socket client) {
        this.threadPool.execute(() -> {
            try {
                this.httpHandler.handleConnection(client.getInputStream(), client.getOutputStream());
            } catch (Exception e) {
                System.err.println("Connection error: " + e.getMessage());
            } finally {
                try {
                    client.close();
                } catch (IOException var10) {
                }

            }

        });
    }

    public void get(String route, RouteRunner runner) {
        this.routes.put(HttpMethod.GET.name() + "::" + route, runner);
    }

    public void post(String route, RouteRunner runner) {
        this.routes.put(HttpMethod.POST.name() + "::" + route, runner);
    }

    public void put(String route, RouteRunner runner) {
        this.routes.put(HttpMethod.PUT.name() + "::" + route, runner);
    }

    public void patch(String route, RouteRunner runner) {
        this.routes.put(HttpMethod.PATCH.name() + "::" + route, runner);
    }

    public void delete(String route, RouteRunner runner) {
        this.routes.put(HttpMethod.DELETE.name() + "::" + route, runner);
    }

    public void stop() throws InterruptedException {
        ExecutorService svc = (ExecutorService)this.threadPool;
        svc.shutdown();
        if (!svc.awaitTermination(30L, TimeUnit.SECONDS)) {
            svc.shutdownNow();
        }

    }
}
