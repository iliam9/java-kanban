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
    public static final Comparator<Task> COMPARE_TASK_BY_START_TIME = Comparator.comparing(Task::getStartTime);

    public InMemoryTasksManager() {
        epics = new HashMap<>();
        subtasks = new HashMap<>();
        tasks = new HashMap<>();

        historyManager = Managers.getDefaultHistory();
        prioritizedTasks = new TreeSet<>(COMPARE_TASK_BY_START_TIME);
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

    public void updateEpicTime(int epicId) {
        List<Integer> subtaskIds = epics.get(epicId).getSubtasksId();//берем список подзадач эпика
        if (subtaskIds.isEmpty()) { // если список пустой устанавливаем нулевые значения для эпика
            epics.get(epicId).setDuration(Duration.ZERO);
            epics.get(epicId).setStartTime(LocalDateTime.now());
            epics.get(epicId).setEndTime(LocalDateTime.now());
            return;
        }
        LocalDateTime epicStartTime = LocalDateTime.now();
        LocalDateTime epicEndTime = LocalDateTime.now();
        Duration epicDuration = Duration.ZERO;
        for (Integer subtaskId : subtaskIds) {
            Subtask subtask = subtasks.get(subtaskId);
            LocalDateTime subtaskStartTime = subtask.getStartTime();
            LocalDateTime subtaskEndTime = subtask.getEndTime();
            if (subtaskStartTime != null) {
                if (epicStartTime == LocalDateTime.now() || subtaskStartTime.isBefore(epicStartTime)) {
                    epicStartTime = subtaskStartTime;
                }
            }
            if (subtaskEndTime != null) {
                if (epicEndTime == LocalDateTime.now() || subtaskEndTime.isAfter(epicEndTime)) {
                    epicEndTime = subtaskEndTime;
                }
            }
            epicDuration = epicDuration.plus(subtasks.get(subtaskId).getDuration());
        }
        epics.get(epicId).setStartTime(epicStartTime);
        epics.get(epicId).setEndTime(epicEndTime);
        epics.get(epicId).setDuration(epicDuration);
    }

    protected void updateEpic(int epicId) {
        updateEpicTime(epicId);
        updateEpicStatus(epicId);
    }

    public void checkValidation(Task newTask) {
        List<Task> prioritizedTasks = getPrioritizedTasks();
        for (Task existTask : prioritizedTasks) {
            if (newTask.getStartTime() == null || existTask.getStartTime() == null) {
                return;
            }
            if (Objects.equals(newTask.getId(), existTask.getId())) {
                continue;
            }
            if ((!newTask.getEndTime().isAfter(existTask.getStartTime())) ||
                    (!newTask.getStartTime().isBefore(existTask.getEndTime()))) {
                continue;
            }
            throw new DataValidationException("Время выполнения задачи пересекается со временем уже существующей " +
                    "задачи. Выберите другую дату.");
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
        checkValidation(task);
        if (task.getId() == 0) {
            task.setId(nextId);
            nextId++;
        }
        tasks.put(task.getId(), task);
        prioritizedTasks.add(task);

    }

    @Override
    public void createSubtask(Subtask subtask) {

        if (Objects.isNull(subtask))
            return;
        checkValidation(subtask);
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
        int id = task.getId();
        checkValidation(task);
        prioritizedTasks.remove(tasks.get(id));
        tasks.put(task.getId(), task);
        prioritizedTasks.add(task);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        int id = subtask.getId();
        checkValidation(subtask);
        prioritizedTasks.remove(subtasks.get(id));
        subtasks.put(id, subtask);
        int epicId = subtasks.get(id).getEpicId();
        updateEpic(epicId);
        prioritizedTasks.add(subtask);

    }

    @Override
    public void updateEpic(Epic epic) {
        checkValidation(epic);
        epics.put(epic.getId(), epic);
        updateEpic(epic.getId());

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

