package service;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.List;

public interface TasksManager {
    void createTask(Task task);

    void createSubtask(Subtask subtask);

    void createEpic(Epic epic);

    void updateTask(Task task);

    void updateSubtask(Subtask subtask);

    void updateEpic(Epic epic);

    Task getTask(int id);

    Subtask getSubtask(int id);

    Epic getEpic(int id);

    List<Task> getTasks();

    List<Subtask> getSubtasks();

    List<Epic> getEpics();

    List<Subtask> getSubtasksFromEpic(int epicId);

    List<Task> getPrioritizedTasks();

    List<Task> getHistory();

    List<Integer> getIdHistory();

    void deleteTask(int taskId);

    void deleteSubtask(int subtaskId);

    void deleteEpic(int epicId);

    void deleteAllTasks();

    void deleteAllSubtasks();

    void deleteAllEpics();
}

