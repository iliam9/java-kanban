package service;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.params.shadow.com.univocity.parsers.common.DataValidationException;
import util.Managers;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTasksManager implements TasksManager {
    private int nextId = 1;
    protected final Map<Integer, Epic> epics;
    protected final Map<Integer, Subtask> subtasks;
    protected final Map<Integer, Task> tasks;
    protected final Set<Task> prioritizedTasks;
    protected final HistoryManager historyManager;

    public InMemoryTasksManager() {
        epics = new HashMap<>();
        subtasks = new HashMap<>();
        tasks = new HashMap<>();

        historyManager = Managers.getDefaultHistory();
        prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));
    }

    protected Status calculateEpicStatus(int epicId) {

        Epic epic = epics.get(epicId);
        if (epic.getSubtasksId().isEmpty())
            return Status.NEW;

        if (epic.getSubtasksId()
                .stream()
                .map(subtasks::get)
                .map(Task::getStatus)
                .allMatch((status) -> status.equals(Status.NEW)))
            return Status.NEW;

        if (epic.getSubtasksId()
                .stream()
                .map(subtasks::get)
                .map(Task::getStatus)
                .allMatch((status) -> status.equals(Status.DONE)))
            return Status.DONE;

        return Status.IN_PROGRESS;
    }

    protected void updateEpicStatus(int epicId) {
        Epic epic = epics.get(epicId);
        Status status = calculateEpicStatus(epicId);
        epic.setStatus(status);
    }

    protected void updateEpicTime(int epicId) {
        Epic epic = epics.get(epicId);

        if (epic.getSubtasksId().isEmpty()) {
            epic.setStartTime(LocalDateTime.now());
            epic.setDuration(Duration.ZERO);
        }

        Optional<LocalDateTime> newStartTime = epic.getSubtasksId()
                .stream()
                .map(subtasks::get)
                .filter(subtask -> subtask.getStatus() != Status.DONE)
                .map(Subtask::getStartTime)
                .min(LocalDateTime::compareTo);

        Optional<LocalDateTime> newEndTime = epic.getSubtasksId()
                .stream()
                .map(subtasks::get)
                .filter(subtask -> subtask.getStatus() != Status.DONE)
                .map(Subtask::getEndTime)
                .max(LocalDateTime::compareTo);

        if (newStartTime.isEmpty() || newEndTime.isEmpty()) {
            return;
        }

        Duration newDuration = Duration.between(newStartTime.get(), newEndTime.get());

        epic.setStartTime(newStartTime.get());
        epic.setDuration(newDuration);
    }

    protected void updateEpic(int epicId) {
        updateEpicTime(epicId);
        updateEpicStatus(epicId);
    }

    protected void checkValidation() {
        final List<Task> prioritizedTask = getPrioritizedTasks();
        for (int i = 0; i < prioritizedTask.size() - 1; i++) {
            final Task currentTask = prioritizedTask.get(i);
            final Task nextTask = prioritizedTask.get(i + 1);

            if (!currentTask.getEndTime().isBefore(nextTask.getStartTime())) {
                throw new DataValidationException("Несколько задач выполняются одновременно.");
            }
        }

    }

    protected void updateId(int id) {
        if (nextId <= id) {
            nextId = id + 1;
        }
    }

    @Override
    public void createTask(Task task) {

        if (Objects.isNull(task))
            return;

        if (task.getId() == 0) {
            task.setId(nextId);
            nextId++;
        }

        tasks.put(task.getId(), task);
        prioritizedTasks.add(task);
        checkValidation();
    }

    @Override
    public void createSubtask(Subtask subtask) {

        if (subtask == null)
            return;

        if (subtask.getId() == 0) {
            subtask.setId(nextId);
            nextId++;
        }

        subtasks.put(subtask.getId(), subtask);

        int epicId = subtask.getEpicId();

        Epic epic = epics.get(epicId);
        epic.addSubtask(subtask.getId());

        updateEpic(epicId);
        prioritizedTasks.add(subtask);
        checkValidation();
    }

    @Override
    public void createEpic(Epic epic) {

        if (Objects.isNull(epic))
            return;

        if (epic.getId() == 0) {
            epic.setId(nextId);
            nextId++;
        }

        epics.put(epic.getId(), epic);
    }

    @Override
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
        checkValidation();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);

        int epicId = subtask.getEpicId();
        updateEpic(epicId);
        checkValidation();
    }

    @Override
    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
        updateEpic(epic.getId());
        checkValidation();
    }

    @Override
    public Task getTask(int id) {
        Task task = tasks.get(id);
        historyManager.add(task);
        return task;
    }

    @Override
    public Subtask getSubtask(int id) {
        Subtask subtask = subtasks.get(id);
        historyManager.add(subtask);
        return subtask;
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = epics.get(id);
        historyManager.add(epic);
        return epic;
    }

    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Subtask> getSubtasksFromEpic(int epicId) {

        if (Objects.isNull(epics.get(epicId))) {
            return null;
        }

        Epic epic = epics.get(epicId);
        List<Integer> subtasksId = epic.getSubtasksId();

        List<Subtask> subtasksFromEpic = new ArrayList<>();
        for (Integer id : subtasksId) {
            subtasksFromEpic.add(subtasks.get(id));
        }

        return subtasksFromEpic;
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    @Override
    public void deleteTask(int taskId) {

        if (Objects.isNull(tasks.get(taskId))) {
            return;
        }

        prioritizedTasks.remove(tasks.get(taskId));
        tasks.remove(taskId);
        historyManager.delete(taskId);
    }

    @Override
    public void deleteSubtask(int subtaskId) {

        if (Objects.isNull(subtasks.get(subtaskId))) {
            return;
        }

        Subtask subtask = subtasks.remove(subtaskId);
        int epicId = subtask.getEpicId();
        Epic epic = epics.get(epicId);

        epic.removeSubtask(subtaskId);

        updateEpic(epicId);

        prioritizedTasks.remove(subtask);
        historyManager.delete(subtaskId);
    }

    @Override
    public void deleteEpic(int epicId) {

        if (Objects.isNull(epics.get(epicId))) {
            return;
        }

        Epic epic = epics.remove(epicId);
        List<Integer> subtasksId = epic.getSubtasksId();
        for (Integer subtaskId : subtasksId) {
            prioritizedTasks.remove(subtasks.get(subtaskId));
            historyManager.delete(subtaskId);
            subtasks.remove(subtaskId);
        }

        historyManager.delete(epicId);
    }

    @Override
    public void deleteAllTasks() {
        for (int taskId : tasks.keySet()) {
            prioritizedTasks.remove(tasks.get(taskId));
            historyManager.delete(taskId);
        }
        tasks.clear();
    }

    @Override
    public void deleteAllSubtasks() {

        for (Epic epic : epics.values()) {
            epic.removeAllSubtasks();
            epic.setStatus(Status.NEW);
            epic.setStartTime(LocalDateTime.now());
            epic.setDuration(Duration.ZERO);
        }

        for (int subtaskId : subtasks.keySet()) {
            prioritizedTasks.remove(subtasks.get(subtaskId));
            historyManager.delete(subtaskId);
        }

        subtasks.clear();
    }

    @Override
    public void deleteAllEpics() {

        for (int subtaskId : subtasks.keySet()) {
            prioritizedTasks.remove(subtasks.get(subtaskId));
            historyManager.delete(subtaskId);
        }

        for (int epicId : epics.keySet()) {
            historyManager.delete(epicId);
        }

        epics.clear();
        subtasks.clear();
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public List<Integer> getIdHistory() {
        return historyManager.getIdHistory();
    }
}

