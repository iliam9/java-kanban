import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import service.FileBackedTasksManager;
import service.HistoryManager;
import util.Managers;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;

import static service.FileBackedTasksManager.loadFromFile;
import static service.FileBackedTasksManager.printAllTasks;

public class Main {
    public static final String PATH = "resources/task.csv";

    public static void main(String[] args) {
        Path path = Paths.get("./resources/task.csv");
        File file = new File(String.valueOf(path));

        FileBackedTasksManager taskManager = Managers.getFileBackedTasksManager(PATH);

        Task task1 = new Task("task1", "taskDescription1", Status.NEW,
                LocalDateTime.parse("2021-06-03T13:54"), Duration.ofMinutes(100));
        Task task2 = new Task("task2", "taskDescription2", Status.NEW,
                LocalDateTime.parse("2021-05-03T13:54"), Duration.ofMinutes(100));
        Task task3 = new Task("task3", "taskDescription3", Status.NEW,
                LocalDateTime.parse("2021-06-03T17:54"), Duration.ofMinutes(100));
        Task task4 = new Task("task4", "taskDescription4", Status.NEW,
                LocalDateTime.parse("2022-06-03T17:54"), Duration.ofMinutes(100));

        Subtask subtask1 = new Subtask("subtask1", "subtaskDescription1", Status.NEW,
                LocalDateTime.parse("2020-06-03T13:54"), Duration.ofMinutes(100), 1);
        Subtask subtask2 = new Subtask("subtask2", "subtaskDescription2", Status.NEW,
                LocalDateTime.parse("2020-05-03T13:56"), Duration.ofMinutes(100), 1);
        Subtask subtask3 = new Subtask("subtask3", "subtaskDescription3", Status.NEW,
                LocalDateTime.parse("2020-06-03T20:00"), Duration.ofMinutes(100), 1);

        Epic epic1 = new Epic("epic1", "epicDescription1");
        Epic epic2 = new Epic("epic2", "epicDescription2");
        Epic epic3 = new Epic("epic3", "taskDescription3");

        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        taskManager.createEpic(epic3);

        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.createSubtask(subtask3);

        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createTask(task3);

        taskManager.getTask(task1.getId());
        taskManager.getTask(task2.getId());
        taskManager.getTask(task3.getId());

        taskManager.getEpic(epic1.getId());
        taskManager.getEpic(epic2.getId());
        taskManager.getEpic(epic3.getId());

        taskManager.getSubtask(subtask1.getId());
        taskManager.getSubtask(subtask2.getId());
        taskManager.getSubtask(subtask3.getId());

        System.out.println("--------------");

        FileBackedTasksManager restoredManager = new FileBackedTasksManager(file);
        restoredManager = loadFromFile(file);
        printAllTasks(restoredManager);
        System.out.println("restored tasks printed");

        restoredManager.createTask(task4);
        System.out.println("-----------------------");

        printAllTasks(restoredManager);

        restoredManager.getTask(task3.getId());

        for (Task item : restoredManager.getHistory()) {
            System.out.print(item.getId() + ",");
        }
        System.out.println("\n");
    }

}

