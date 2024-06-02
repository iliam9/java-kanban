package http;

import com.sun.net.httpserver.HttpServer;
import exceptions.StartServerException;
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
            throw new StartServerException("Server start error.");
        }

        httpServer.createContext("/tasks", new TasksHandler(this));
    }

    public void start() {
        String s = String.format("Запускаем сервер для работы с задачами на порту %s", port);
        System.out.println(s);
        String s1 = String.format("URL http://localhost: %s /", port);
        System.out.println(s1);
        httpServer.start();
    }

    public void stop() {
        httpServer.stop(0);
    }

    public HttpTaskManager getTasksManager() {
        return tasksManager;
    }
}