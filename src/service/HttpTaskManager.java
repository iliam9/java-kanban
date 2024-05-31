package service;

import httpServer.KVTaskClient;
import httpServer.TaskGson;
import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskType;

import java.net.URL;
import java.util.*;

public class HttpTaskManager extends InMemoryTasksManager {
    private final KVTaskClient kvTaskClient;

    public HttpTaskManager(URL urlDataServer) {
        super();
        kvTaskClient = new KVTaskClient(urlDataServer);
        loadFromServer();
    }

    public void loadFromServer() {
        Map<Integer, TaskType> idToType = new HashMap<>();
        String response = kvTaskClient.load("tasks");
        List<Task> loadedTasks;
        if (!response.isEmpty())
            loadedTasks = new ArrayList<>(List.of(
                    TaskGson.GSON.fromJson(response, Task[].class)));
        else {
            loadedTasks = Collections.emptyList();
        }
        for (Task task : loadedTasks) {
            super.updateId(task.getId());
            tasks.put(task.getId(), task);
            prioritizedTasks.add(task);
            idToType.put(task.getId(), TaskType.TASK);
        }

        response = kvTaskClient.load("epics");
        List<Epic> loadedEpics;
        if (!response.isEmpty()) {
            loadedEpics = new ArrayList<>(List.of(
                    TaskGson.GSON.fromJson(response, Epic[].class)));
        } else {
            loadedEpics = Collections.emptyList();
        }
        for (Epic epic : loadedEpics) {
            super.updateId(epic.getId());
            tasks.put(epic.getId(), epic);
            idToType.put(epic.getId(), TaskType.EPIC);
        }

        response = kvTaskClient.load("subtasks");
        List<Subtask> loadedSubtasks;
        if (!response.isEmpty()) {
            loadedSubtasks = new ArrayList<>(List.of(
                    TaskGson.GSON.fromJson(response, Subtask[].class)));
        } else {
            loadedSubtasks = Collections.emptyList();
        }
        for (Subtask subtask : loadedSubtasks) {
            super.updateId(subtask.getId());
            tasks.put(subtask.getId(), subtask);

            int epicId = subtask.getEpicId();

            Epic epic = epics.get(epicId);
            epic.addSubtask(subtask.getId());

            updateEpic(epicId);
            prioritizedTasks.add(subtask);
            idToType.put(subtask.getId(), TaskType.SUBTASK);
        }

        response = kvTaskClient.load("history");
        List<Integer> historyTasksId;
        if (!response.isEmpty()) {
            historyTasksId = List.of(TaskGson.GSON.fromJson(
                    response, Integer[].class));
        } else {
            historyTasksId = Collections.emptyList();
        }
        for (Integer id : historyTasksId) {

            Task task;
            TaskType taskType = idToType.get(id);

            switch (taskType) {
                case TASK:
                    task = getTask(id);
                    break;
                case EPIC:
                    task = getEpic(id);
                    break;
                case SUBTASK:
                    task = getSubtask(id);
                    break;
                default:
                    throw new RuntimeException();
            }
            historyManager.add(task);
        }
    }

    public void save() {
        kvTaskClient.put("epics", TaskGson.GSON.toJson(epics));
        kvTaskClient.put("tasks", TaskGson.GSON.toJson(tasks));
        kvTaskClient.put("subtasks", TaskGson.GSON.toJson(subtasks));
        kvTaskClient.put("history", TaskGson.GSON.toJson(getIdHistory()));
    }

    @Override
    public void createTask(Task task) {
        super.createTask(task);
        save();
    }

    @Override
    public void createSubtask(Subtask subtask) {
        super.createSubtask(subtask);
        save();
    }

    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public Task getTask(int id) {
        Task task = super.getTask(id);
        save();
        return task;
    }

    @Override
    public Subtask getSubtask(int id) {
        Subtask subtask = super.getSubtask(id);
        save();
        return subtask;
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = super.getEpic(id);
        save();
        return epic;
    }

    @Override
    public List<Task> getTasks() {
        List<Task> tasks = super.getTasks();
        save();
        return tasks;
    }

    @Override
    public List<Subtask> getSubtasks() {
        List<Subtask> subtasks = super.getSubtasks();
        save();
        return subtasks;
    }

    @Override
    public List<Epic> getEpics() {
        List<Epic> epics = super.getEpics();
        save();
        return epics;
    }

    @Override
    public void deleteTask(int taskId) {
        super.deleteTask(taskId);
        save();
    }

    @Override
    public void deleteSubtask(int subtaskId) {
        super.deleteSubtask(subtaskId);
        save();
    }

    @Override
    public void deleteEpic(int epicId) {
        super.deleteEpic(epicId);
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public List<Subtask> getSubtasksFromEpic(int epicId) {
        List<Subtask> subtasks = super.getSubtasksFromEpic(epicId);
        save();
        return subtasks;
    }

}
