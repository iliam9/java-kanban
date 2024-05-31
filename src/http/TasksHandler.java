package http;

import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.Epic;
import model.Subtask;
import model.Task;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

class TasksHandler implements HttpHandler {
    private final HttpTaskServer httpTaskServer;
    private final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    public TasksHandler(HttpTaskServer httpTaskServer) {
        this.httpTaskServer = httpTaskServer;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        System.out.println("Началась обработка /tasks запроса от клиента.");

        Endpoint endpoint = getEndpoint(httpExchange.getRequestURI().getPath(),
                httpExchange.getRequestMethod());

        System.out.println("Эндпоинт " + endpoint.toString());
        switch (endpoint) {
            case PUT_TASK:
                handlePutTask(httpExchange);
                break;
            case PUT_SUBTASK:
                handlePutSubtask(httpExchange);
                break;
            case PUT_EPIC:
                handlePutEpic(httpExchange);
                break;
            case GET_TASK:
                handleGetTask(httpExchange);
                break;
            case GET_SUBTASK:
                handleGetSubtask(httpExchange);
                break;
            case GET_EPIC:
                handleGetEpic(httpExchange);
                break;
            case GET_TASKS:
                handleGetTasks(httpExchange);
                break;
            case GET_SUBTASKS:
                handleGetSubtasks(httpExchange);
                break;
            case GET_EPICS:
                handleGetEpics(httpExchange);
                break;
            case GET_SUBTASKS_FROM_EPIC:
                handleGetSubtasksFromEpic(httpExchange);
                break;
            case GET_PRIORITIZED_TASKS:
                handleGetPrioritizedTasks(httpExchange);
                break;
            case GET_HISTORY:
                handleGetHistory(httpExchange);
                break;
            case DELETE_TASK:
                handleDeleteTask(httpExchange);
                break;
            case DELETE_SUBTASK:
                handleDeleteSubtask(httpExchange);
                break;
            case DELETE_EPIC:
                handleDeleteEpic(httpExchange);
                break;
            case DELETE_TASKS:
                handleDeleteTasks(httpExchange);
                break;
            case DELETE_SUBTASKS:
                handleDeleteSubtasks(httpExchange);
                break;
            case DELETE_EPICS:
                handleDeleteEpics(httpExchange);
                break;
            default:
                writeResponse(httpExchange, "Такого эндпоинта не существует", 404);
        }
    }

    private void handlePutTask(HttpExchange httpExchange) throws IOException {
        String body = readBody(httpExchange);
        Task task;
        try {
            task = TaskGson.GSON.fromJson(body, Task.class);
        } catch (JsonSyntaxException ex) {
            writeResponse(httpExchange, "Получен некорректный JSON", 400);
            return;
        }

        if (task.getId() == 0) {
            httpTaskServer.getTasksManager().createTask(task);
            writeResponse(httpExchange, "Задача создана", 201);
            System.out.println("Задача создана.");
        } else {
            httpTaskServer.getTasksManager().updateTask(task);
            writeResponse(httpExchange, "Задача обновлена", 201);
            System.out.println("Задача обновлена.");
        }
    }

    private void handlePutSubtask(HttpExchange httpExchange) throws IOException {
        String body = readBody(httpExchange);

        Subtask subtask;
        try {
            subtask = TaskGson.GSON.fromJson(body, Subtask.class);
        } catch (JsonSyntaxException ex) {
            writeResponse(httpExchange, "Получен некорректный JSON", 400);
            return;
        }

        if (subtask.getId() == 0) {
            httpTaskServer.getTasksManager().createSubtask(subtask);
            writeResponse(httpExchange, "Подзадача создана", 201);
        } else {
            httpTaskServer.getTasksManager().updateTask(subtask);
            writeResponse(httpExchange, "Подзадача обновлена", 201);
        }
    }

    private void handlePutEpic(HttpExchange httpExchange) throws IOException {
        String body = readBody(httpExchange);

        Epic epic;
        try {
            epic = TaskGson.GSON.fromJson(body, Epic.class);
        } catch (JsonSyntaxException ex) {
            writeResponse(httpExchange, "Получен некорректный JSON", 400);
            return;
        }

        if (epic.getId() == 0) {
            httpTaskServer.getTasksManager().createEpic(epic);
            writeResponse(httpExchange, "Эпик создана", 201);
        } else {
            httpTaskServer.getTasksManager().updateEpic(epic);
            writeResponse(httpExchange, "Эпик обновлен", 201);
        }
    }

    private void handleGetTask(HttpExchange httpExchange) throws IOException {
        String pathId = httpExchange.getRequestURI().getRawQuery().replaceAll("id=", "");
        int id = getId(pathId);
        Task task = httpTaskServer.getTasksManager().getTask(id);
        writeResponse(httpExchange, TaskGson.GSON.toJson(task), 200);
        System.out.println("Задача успешно отправлена.");
    }

    private void handleGetSubtask(HttpExchange httpExchange) throws IOException {
        String pathId = httpExchange.getRequestURI().getRawQuery().replaceAll("id=", "");
        int id = getId(pathId);
        Subtask subtask = httpTaskServer.getTasksManager().getSubtask(id);
        writeResponse(httpExchange, TaskGson.GSON.toJson(subtask), 200);
        System.out.println("Подзадача успешно отправлена.");
    }

    private void handleGetEpic(HttpExchange httpExchange) throws IOException {
        String pathId = httpExchange.getRequestURI().getRawQuery().replaceAll("id=", "");
        int id = getId(pathId);
        Epic epic = httpTaskServer.getTasksManager().getEpic(id);
        writeResponse(httpExchange, TaskGson.GSON.toJson(epic), 200);
        System.out.println("Эпик успешно отправлен.");
    }

    private void handleGetTasks(HttpExchange httpExchange) throws IOException {
        writeResponse(httpExchange, TaskGson.GSON.toJson(httpTaskServer.getTasksManager().getTasks()), 200);
        System.out.println("Задачи успешно отправлены.");
    }

    private void handleGetSubtasks(HttpExchange httpExchange) throws IOException {
        writeResponse(httpExchange, TaskGson.GSON.toJson(httpTaskServer.getTasksManager().getSubtasks()), 200);
        System.out.println("Подзадачи успешно отправлены.");
    }

    private void handleGetEpics(HttpExchange httpExchange) throws IOException {
        writeResponse(httpExchange, TaskGson.GSON.toJson(httpTaskServer.getTasksManager().getEpics()), 200);
        System.out.println("Эпики успешно отправлены.");
    }

    private void handleGetSubtasksFromEpic(HttpExchange httpExchange) throws IOException {
        String pathId = httpExchange.getRequestURI().getRawQuery().replaceAll("id=", "");
        int id = getId(pathId);
        writeResponse(httpExchange, TaskGson.GSON.toJson(httpTaskServer.getTasksManager().getSubtasksFromEpic(id)), 200);
        System.out.println("Подзадачи эпика успешно отправлены.");
    }

    private void handleGetPrioritizedTasks(HttpExchange httpExchange) throws IOException {
        writeResponse(httpExchange, TaskGson.GSON.toJson(httpTaskServer.getTasksManager().getPrioritizedTasks()), 200);
        System.out.println("Отсортированные задачи успешно отправлены.");
    }

    private void handleGetHistory(HttpExchange httpExchange) throws IOException {
        writeResponse(httpExchange, TaskGson.GSON.toJson(httpTaskServer.getTasksManager().getHistory()), 200);
        System.out.println("История упешно отправлена.");
    }

    private void handleDeleteTask(HttpExchange httpExchange) throws IOException {
        String pathId = httpExchange.getRequestURI().getRawQuery().replaceAll("id=", "");
        int id = getId(pathId);
        httpTaskServer.getTasksManager().deleteTask(id);
        writeResponse(httpExchange, "Задача успешно удалена.", 200);
        System.out.println("Задача успешно удалена.");
    }

    private void handleDeleteSubtask(HttpExchange httpExchange) throws IOException {
        String pathId = httpExchange.getRequestURI().getRawQuery().replaceAll("id=", "");
        int id = getId(pathId);
        httpTaskServer.getTasksManager().deleteSubtask(id);
        writeResponse(httpExchange, "Подзадача успешно удалена.", 200);
        System.out.println("Подзадача успешно удалена.");
    }

    private void handleDeleteEpic(HttpExchange httpExchange) throws IOException {
        String pathId = httpExchange.getRequestURI().getRawQuery().replaceAll("id=", "");
        int id = getId(pathId);
        httpTaskServer.getTasksManager().deleteEpic(id);
        writeResponse(httpExchange, "Эпик успешно удален.", 200);
        System.out.println("Эпик успешно удален.");
    }

    private void handleDeleteTasks(HttpExchange httpExchange) throws IOException {
        httpTaskServer.getTasksManager().deleteAllTasks();
        writeResponse(httpExchange, "Все задачи успешно удалены.", 200);
        System.out.println("Все задачи успешно удалены.");
    }

    private void handleDeleteSubtasks(HttpExchange httpExchange) throws IOException {
        httpTaskServer.getTasksManager().deleteAllSubtasks();
        writeResponse(httpExchange, "Все подзадачи успешно удалены.", 200);
        System.out.println("Все подзадачи успешно удалены.");
    }

    private void handleDeleteEpics(HttpExchange httpExchange) throws IOException {
        httpTaskServer.getTasksManager().deleteAllEpics();
        writeResponse(httpExchange, "Все эпики успешно удалены.", 200);
        System.out.println("Все эпики успешно удалены.");
    }

    private Endpoint getEndpoint(String requestPath, String requestMethod) {
        try {
            switch (requestMethod) {
                case "GET":
                    if (Pattern.matches("^/tasks$", requestPath)) {
                        return Endpoint.GET_PRIORITIZED_TASKS;
                    }
                    if (Pattern.matches("^/tasks/history$", requestPath)) {
                        return Endpoint.GET_HISTORY;
                    }
                    if (Pattern.matches("^/tasks/task[\\s\\S]*", requestPath)) {
                        if (Pattern.matches("^/tasks/task$", requestPath)) {
                            return Endpoint.GET_TASKS;
                        } else {
                            return Endpoint.GET_TASK;
                        }
                    } else if (Pattern.matches("^/tasks/subtask[\\s\\S]*", requestPath)) {
                        if (Pattern.matches("^/tasks/subtask$", requestPath)) {
                            return Endpoint.GET_SUBTASKS;
                        } else if (Pattern.matches("^/tasks/subtask/epic[\\s\\S]*", requestPath)) {
                            return Endpoint.GET_SUBTASKS_FROM_EPIC;
                        } else {
                            return Endpoint.GET_SUBTASK;
                        }
                    } else if (Pattern.matches("^/tasks/epic[\\s\\S]*", requestPath)) {
                        if (Pattern.matches("^/tasks/epic$", requestPath)) {
                            return Endpoint.GET_EPICS;
                        } else {
                            return Endpoint.GET_EPIC;
                        }
                    } else {
                        return Endpoint.UNKNOWN;
                    }
                case "POST":
                    if (Pattern.matches("^/tasks/task$", requestPath)) {
                        return Endpoint.PUT_TASK;
                    } else if (Pattern.matches("^/tasks/subtask$", requestPath)) {
                        return Endpoint.PUT_SUBTASK;
                    } else if (Pattern.matches("^/tasks/epic$", requestPath)) {
                        return Endpoint.PUT_EPIC;
                    } else {
                        return Endpoint.UNKNOWN;
                    }
                case "DELETE":
                    if (Pattern.matches("^/tasks/task[\\s\\S]*", requestPath)) {
                        if (Pattern.matches("^/tasks/task$", requestPath)) {
                            return Endpoint.DELETE_TASKS;
                        } else {
                            return Endpoint.DELETE_TASK;
                        }
                    } else if (Pattern.matches("^/tasks/subtask[\\s\\S]*", requestPath)) {
                        if (Pattern.matches("^/tasks/subtask$", requestPath)) {
                            return Endpoint.DELETE_SUBTASKS;
                        } else {
                            return Endpoint.DELETE_SUBTASK;
                        }
                    } else if (Pattern.matches("^/tasks/epic[\\s\\S]*", requestPath)) {
                        if (Pattern.matches("^/tasks/epic$", requestPath)) {
                            return Endpoint.DELETE_EPICS;
                        } else {
                            return Endpoint.DELETE_EPIC;
                        }
                    } else {
                        return Endpoint.UNKNOWN;
                    }
                default:
                    System.out.println("Неизвестный запрос");
                    return Endpoint.UNKNOWN;
            }
        } catch (JsonSyntaxException | NullPointerException ex) {
            System.out.println("Json объект передается в неверном формате.");
            return Endpoint.UNKNOWN;
        }
    }

    private void writeResponse(HttpExchange exchange,
                               String responseString,
                               int responseCode) throws IOException {
        if (responseString.isBlank()) {
            exchange.sendResponseHeaders(responseCode, 0);
        } else {
            byte[] bytes = responseString.getBytes(DEFAULT_CHARSET);
            exchange.sendResponseHeaders(responseCode, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        }
        exchange.close();
    }

    private String readBody(HttpExchange httpExchange) throws IOException {
        try (InputStream os = httpExchange.getRequestBody()) {
            return new String(os.readAllBytes(), DEFAULT_CHARSET);
        } catch (JsonSyntaxException exp) {
            writeResponse(httpExchange, "Получен некорректный JSON", 400);
            return "";
        }
    }

    private int getId(String pathWithId) {
        return Integer.parseInt(pathWithId);
    }

}
