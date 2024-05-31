package httpServer;

import com.sun.net.httpserver.HttpServer;
import service.HttpTaskManager;
import util.Managers;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    private final HttpServer httpServer;
    private final int port;

    private final HttpTaskManager tasksManager;

    public HttpTaskServer(int httpTaskServerPort, int dataPort) {
        this.port = httpTaskServerPort;
        tasksManager = Managers.getHttpTaskManager(dataPort);

        try {
            httpServer = HttpServer.create();
            httpServer.bind(new InetSocketAddress(port), 0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        httpServer.createContext("/tasks", new TasksHandler(this));
    }

    public void start() {
        System.out.println("Запускаем сервер для работы с задачами на порту " + port);
        System.out.println("URL http://localhost:" + port + "/");
        httpServer.start();
    }

    public void stop() {
        httpServer.stop(0);
    }

    public HttpTaskManager getTasksManager() {
        return tasksManager;
    }
}