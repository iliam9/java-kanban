package httpServerTest;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import httpServer.HttpTaskServer;
import httpServer.KVServer;
import httpServer.TaskGson;
import model.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.TasksManager;
import util.Managers;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskServerTest {

    private KVServer kvServer;

    private HttpTaskServer httpTaskServer;

    private final HttpClient client = HttpClient.newHttpClient();

    private final String urlInString = "http://localhost:8082/tasks";

    private Task task1, task2, task3;
    private Epic epic1, epic2, epic3;
    private Subtask subtask1, subtask2, subtask3;

    @BeforeEach
    public void startServers() {
        httpTaskServer = new HttpTaskServer(8082, 8083);
        kvServer = new KVServer(8083);
        kvServer.start();
        httpTaskServer.start();
    }

    @BeforeEach
    public void initTasks() {
        task1 = new Task("task1", "taskDescription1", Status.NEW
                , LocalDateTime.parse("2021-06-03T13:54"), Duration.ofMinutes(100));
        task2 = new Task("task2", "taskDescription2", Status.NEW
                , LocalDateTime.parse("2021-05-03T13:54"), Duration.ofMinutes(100));
        task3 = new Task("task3", "taskDescription3", Status.NEW
                , LocalDateTime.parse("2021-06-03T17:54"), Duration.ofMinutes(100));

        subtask1 = new Subtask("subtask1", "subtaskDescription1", Status.NEW
                , LocalDateTime.parse("2020-06-03T13:54"), Duration.ofMinutes(100), 1);
        subtask2 = new Subtask("subtask2", "subtaskDescription2", Status.NEW,
                LocalDateTime.parse("2020-05-03T13:56"), Duration.ofMinutes(100), 1);
        subtask3 = new Subtask("subtask3", "subtaskDescription3", Status.NEW,
                LocalDateTime.parse("2020-06-03T20:00"), Duration.ofMinutes(100), 1);

        epic1 = new Epic("epic1", "epicDescription1");
        epic2 = new Epic("epic2", "epicDescription2");
        epic3 = new Epic("epic3", "taskDescription3");
    }

    @AfterEach
    public void stopServers() {
        kvServer.stop();
        httpTaskServer.stop();
    }

    @Test
    public void shouldGetEmptyListTask() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlInString))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        JsonElement jsonElement = JsonParser.parseString(response.body());
        if (!jsonElement.isJsonArray()) {
            System.out.println("Ответ от сервера не соответствует ожидаемому.");
            return;
        }

        JsonArray jsonArray = jsonElement.getAsJsonArray();
        assertTrue(jsonArray.isEmpty());
    }

    @Test
    public void shouldCreateAndGetTask() throws IOException, InterruptedException {
        HttpResponse<String> response = sendTask(task1, TaskType.TASK);
        assertEquals(201, response.statusCode());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlInString + "/task/?id=1"))
                .GET()
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        JsonElement jsonElement = JsonParser.parseString(response.body());
        assertTrue(jsonElement.isJsonObject());

        JsonObject jsonObject = jsonElement.getAsJsonObject();
        Task loadedTask = TaskGson.GSON.fromJson(jsonObject, Task.class);
        assertEquals(task1, loadedTask);
    }

    @Test
    public void shouldCreateAndGetEpic() throws IOException, InterruptedException {
        HttpResponse<String> response = sendTask(epic1, TaskType.EPIC);
        assertEquals(201, response.statusCode());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlInString + "/epic/?id=1"))
                .GET()
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        JsonElement jsonElement = JsonParser.parseString(response.body());
        assertTrue(jsonElement.isJsonObject());

        JsonObject jsonObject = jsonElement.getAsJsonObject();
        Epic loadedEpic = TaskGson.GSON.fromJson(jsonObject, Epic.class);
        assertEquals(epic1, loadedEpic);
    }

    @Test
    public void shouldCreateAndGetSubtask() throws IOException, InterruptedException {

        HttpResponse<String> response = sendTask(epic1, TaskType.EPIC);
        assertEquals(201, response.statusCode());

        response = sendTask(subtask1, TaskType.SUBTASK);
        assertEquals(201, response.statusCode());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlInString + "/subtask/?id=2"))
                .GET()
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        JsonElement jsonElement = JsonParser.parseString(response.body());
        assertTrue(jsonElement.isJsonObject());

        JsonObject jsonObject = jsonElement.getAsJsonObject();
        Subtask loadedSubtask = TaskGson.GSON.fromJson(jsonObject, Subtask.class);
        assertEquals(subtask1, loadedSubtask);
    }

    @Test
    public void shouldGetTasks() throws IOException, InterruptedException {

        HttpResponse<String> response = sendTask(task1, TaskType.TASK);
        assertEquals(201, response.statusCode());

        response = sendTask(task2, TaskType.TASK);
        assertEquals(201, response.statusCode());

        response = sendTask(task3, TaskType.TASK);
        assertEquals(201, response.statusCode());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlInString + "/task"))
                .GET()
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        JsonElement jsonElement = JsonParser.parseString(response.body());
        assertTrue(jsonElement.isJsonArray());

        JsonArray jsonArray = jsonElement.getAsJsonArray();
        Task[] loadedTasks = TaskGson.GSON.fromJson(jsonArray, Task[].class);
        assertArrayEquals(loadedTasks, new Task[]{task1, task2, task3});
    }

    @Test
    public void shouldGetEpics() throws IOException, InterruptedException {

        HttpResponse<String> response = sendTask(epic1, TaskType.EPIC);
        assertEquals(201, response.statusCode());

        response = sendTask(epic2, TaskType.EPIC);
        assertEquals(201, response.statusCode());

        response = sendTask(epic3, TaskType.EPIC);
        assertEquals(201, response.statusCode());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlInString + "/epic"))
                .GET()
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        JsonElement jsonElement = JsonParser.parseString(response.body());
        assertTrue(jsonElement.isJsonArray());

        JsonArray jsonArray = jsonElement.getAsJsonArray();
        Epic[] loadedTasks = TaskGson.GSON.fromJson(jsonArray, Epic[].class);
        assertArrayEquals(loadedTasks, new Epic[]{epic1, epic2, epic3});
    }

    @Test
    public void shouldGetSubtasks() throws IOException, InterruptedException {

        HttpResponse<String> response = sendTask(epic1, TaskType.EPIC);
        assertEquals(201, response.statusCode());

        response = sendTask(subtask1, TaskType.SUBTASK);
        assertEquals(201, response.statusCode());

        response = sendTask(subtask2, TaskType.SUBTASK);
        assertEquals(201, response.statusCode());

        response = sendTask(subtask3, TaskType.SUBTASK);
        assertEquals(201, response.statusCode());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlInString + "/subtask"))
                .GET()
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        JsonElement jsonElement = JsonParser.parseString(response.body());
        assertTrue(jsonElement.isJsonArray());

        JsonArray jsonArray = jsonElement.getAsJsonArray();
        Subtask[] loadedSubtasks = TaskGson.GSON.fromJson(jsonArray, Subtask[].class);
        assertArrayEquals(loadedSubtasks, new Subtask[]{subtask1, subtask2, subtask3});
    }

    @Test
    public void shouldGetSubtasksFromEpic() throws IOException, InterruptedException {

        HttpResponse<String> response = sendTask(epic1, TaskType.EPIC);
        assertEquals(201, response.statusCode());

        subtask1.setEpicId(1);
        response = sendTask(subtask1, TaskType.SUBTASK);
        assertEquals(201, response.statusCode());

        subtask2.setEpicId(1);
        response = sendTask(subtask2, TaskType.SUBTASK);
        assertEquals(201, response.statusCode());

        subtask3.setEpicId(1);
        response = sendTask(subtask3, TaskType.SUBTASK);
        assertEquals(201, response.statusCode());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlInString + "/subtask/epic/?id=1"))
                .GET()
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        JsonElement jsonElement = JsonParser.parseString(response.body());
        assertTrue(jsonElement.isJsonArray());

        JsonArray jsonArray = jsonElement.getAsJsonArray();
        Subtask[] loadedSubtasks = TaskGson.GSON.fromJson(jsonArray, Subtask[].class);
        assertArrayEquals(loadedSubtasks, new Subtask[]{subtask1, subtask2, subtask3});

    }

    @Test
    public void shouldReturnPrioritizedTasks() throws IOException, InterruptedException {
        task1.setStartTime(LocalDateTime.parse("2023-01-01T00:00"));
        task2.setStartTime(LocalDateTime.parse("2022-01-01T00:00"));
        task3.setStartTime(LocalDateTime.parse("2022-01-01T10:00"));

        subtask1.setStartTime(LocalDateTime.parse("2022-10-01T00:00"));
        subtask2.setStartTime(LocalDateTime.parse("2025-01-01T00:00"));
        subtask3.setStartTime(LocalDateTime.parse("2021-01-01T00:01"));

        HttpResponse<String> response;

        response = sendTask(epic1, TaskType.EPIC);
        assertEquals(201, response.statusCode());

        response = sendTask(task1, TaskType.TASK);
        assertEquals(201, response.statusCode());

        response = sendTask(task2, TaskType.TASK);
        assertEquals(201, response.statusCode());

        response = sendTask(task3, TaskType.TASK);
        assertEquals(201, response.statusCode());

        response = sendTask(subtask1, TaskType.SUBTASK);
        assertEquals(201, response.statusCode());

        response = sendTask(subtask2, TaskType.SUBTASK);
        assertEquals(201, response.statusCode());

        response = sendTask(subtask3, TaskType.SUBTASK);
        assertEquals(201, response.statusCode());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlInString))
                .GET()
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        JsonElement jsonElement = JsonParser.parseString(response.body());
        assertTrue(jsonElement.isJsonArray());

        JsonArray jsonArray = jsonElement.getAsJsonArray();

        TasksManager manager = Managers.getInMemoryTasksManager();
        manager.createEpic(epic1);
        manager.createTask(task1);
        manager.createTask(task2);
        manager.createTask(task3);
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);
        manager.createSubtask(subtask3);
        assertEquals(TaskGson.GSON.toJson(List.of(subtask3, task2, task3, subtask1, task1, subtask2)), TaskGson.GSON.toJson(jsonArray));
    }

    @Test
    public void shouldReturnHistory() throws IOException, InterruptedException {

        HttpResponse<String> response = sendTask(epic1, TaskType.EPIC);
        assertEquals(201, response.statusCode());

        response = sendTask(task1, TaskType.TASK);
        assertEquals(201, response.statusCode());

        response = sendTask(subtask1, TaskType.SUBTASK);
        assertEquals(201, response.statusCode());


        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlInString + "/task/?id=2"))
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        request = HttpRequest.newBuilder()
                .uri(URI.create(urlInString + "/epic/?id=1"))
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        request = HttpRequest.newBuilder()
                .uri(URI.create(urlInString + "/subtask/?id=3"))
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());


        request = HttpRequest.newBuilder()
                .uri(URI.create(urlInString + "/history"))
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        JsonElement jsonElement = JsonParser.parseString(response.body());
        assertTrue(jsonElement.isJsonArray());

        JsonArray jsonArray = jsonElement.getAsJsonArray();

        TasksManager manager = Managers.getInMemoryTasksManager();
        manager.createEpic(epic1);
        manager.createTask(task1);
        manager.createSubtask(subtask1);
        manager.getTask(2);
        manager.getEpic(1);
        manager.getSubtask(3);
        assertEquals(TaskGson.GSON.toJson(manager.getHistory()), TaskGson.GSON.toJson(jsonArray));
    }

    @Test
    public void shouldDeleteTask() throws IOException, InterruptedException {
        HttpResponse<String> response = sendTask(task1, TaskType.TASK);
        assertEquals(201, response.statusCode());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlInString + "/task/?id=1"))
                .DELETE()
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        request = HttpRequest.newBuilder()
                .uri(URI.create(urlInString + "/task"))
                .GET()
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        JsonElement jsonElement = JsonParser.parseString(response.body());
        assertTrue(jsonElement.isJsonArray());

        JsonArray jsonArray = jsonElement.getAsJsonArray();
        Task[] loadedTasks = TaskGson.GSON.fromJson(jsonArray, Task[].class);
        assertArrayEquals(loadedTasks, new Task[]{});
    }

    @Test
    public void shouldDeleteTasks() throws IOException, InterruptedException {
        HttpResponse<String> response = sendTask(task1, TaskType.TASK);
        assertEquals(201, response.statusCode());

        response = sendTask(task2, TaskType.TASK);
        assertEquals(201, response.statusCode());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlInString + "/task"))
                .DELETE()
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        request = HttpRequest.newBuilder()
                .uri(URI.create(urlInString + "/task"))
                .GET()
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        JsonElement jsonElement = JsonParser.parseString(response.body());
        assertTrue(jsonElement.isJsonArray());

        JsonArray jsonArray = jsonElement.getAsJsonArray();
        Task[] loadedTasks = TaskGson.GSON.fromJson(jsonArray, Task[].class);
        assertArrayEquals(loadedTasks, new Task[]{});
    }

    @Test
    public void shouldDeleteEpic() throws IOException, InterruptedException {
        HttpResponse<String> response = sendTask(epic1, TaskType.EPIC);
        assertEquals(201, response.statusCode());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlInString + "/epic/?id=1"))
                .DELETE()
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        request = HttpRequest.newBuilder()
                .uri(URI.create(urlInString + "/epic"))
                .GET()
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        JsonElement jsonElement = JsonParser.parseString(response.body());
        assertTrue(jsonElement.isJsonArray());

        JsonArray jsonArray = jsonElement.getAsJsonArray();
        Epic[] loadedTasks = TaskGson.GSON.fromJson(jsonArray, Epic[].class);
        assertArrayEquals(loadedTasks, new Epic[]{});
    }

    @Test
    public void shouldDeleteEpics() throws IOException, InterruptedException {
        HttpResponse<String> response = sendTask(epic1, TaskType.EPIC);
        assertEquals(201, response.statusCode());

        response = sendTask(epic2, TaskType.EPIC);
        assertEquals(201, response.statusCode());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlInString + "/epic"))
                .DELETE()
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        request = HttpRequest.newBuilder()
                .uri(URI.create(urlInString + "/epic"))
                .GET()
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        JsonElement jsonElement = JsonParser.parseString(response.body());
        assertTrue(jsonElement.isJsonArray());

        JsonArray jsonArray = jsonElement.getAsJsonArray();
        Epic[] loadedTasks = TaskGson.GSON.fromJson(jsonArray, Epic[].class);
        assertArrayEquals(loadedTasks, new Epic[]{});
    }

    @Test
    public void shouldDeleteSubtask() throws IOException, InterruptedException {
        HttpResponse<String> response = sendTask(epic1, TaskType.EPIC);
        assertEquals(201, response.statusCode());

        response = sendTask(subtask1, TaskType.SUBTASK);
        assertEquals(201, response.statusCode());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlInString + "/subtask/?id=2"))
                .DELETE()
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        request = HttpRequest.newBuilder()
                .uri(URI.create(urlInString + "/subtask"))
                .GET()
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        JsonElement jsonElement = JsonParser.parseString(response.body());
        assertTrue(jsonElement.isJsonArray());

        JsonArray jsonArray = jsonElement.getAsJsonArray();
        Subtask[] loadedTasks = TaskGson.GSON.fromJson(jsonArray, Subtask[].class);
        assertArrayEquals(loadedTasks, new Subtask[]{});
    }

    @Test
    public void shouldDeleteSubtasks() throws IOException, InterruptedException {
        HttpResponse<String> response = sendTask(epic1, TaskType.EPIC);
        assertEquals(201, response.statusCode());

        response = sendTask(subtask1, TaskType.SUBTASK);
        assertEquals(201, response.statusCode());

        response = sendTask(subtask2, TaskType.SUBTASK);
        assertEquals(201, response.statusCode());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlInString + "/subtask"))
                .DELETE()
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        request = HttpRequest.newBuilder()
                .uri(URI.create(urlInString + "/subtask"))
                .GET()
                .build();

        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        JsonElement jsonElement = JsonParser.parseString(response.body());
        assertTrue(jsonElement.isJsonArray());

        JsonArray jsonArray = jsonElement.getAsJsonArray();
        Subtask[] loadedTasks = TaskGson.GSON.fromJson(jsonArray, Subtask[].class);
        assertArrayEquals(loadedTasks, new Subtask[]{});
    }

    private HttpResponse<String> sendTask(Task task, TaskType type) throws IOException, InterruptedException {
        String json = TaskGson.GSON.toJson(task);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request;
        switch (type) {
            case TASK:
                request = HttpRequest.newBuilder()
                        .uri(URI.create(urlInString + "/task"))
                        .POST(body)
                        .build();
                break;
            case SUBTASK:
                request = HttpRequest.newBuilder()
                        .uri(URI.create(urlInString + "/subtask"))
                        .POST(body)
                        .build();
                break;
            case EPIC:
                request = HttpRequest.newBuilder()
                        .uri(URI.create(urlInString + "/epic"))
                        .POST(body)
                        .build();
                break;
            default:
                throw new IllegalArgumentException();
        }
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }
}
