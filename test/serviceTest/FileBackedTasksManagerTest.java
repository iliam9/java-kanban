package serviceTest;

import exceptions.ManagerSaveException;
import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.FileBackedTasksManager;
import service.TasksManager;
import util.Managers;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FileBackedTasksManagerTest extends TasksManagerTest<FileBackedTasksManager> {

    public static final String PATH_TO_DATA_TEST = "test/serviceTest/resources/taskTest.csv";

    @BeforeEach
    public void initManager() {
        taskManager = (FileBackedTasksManager) Managers.getFileBackedTasksManager(PATH_TO_DATA_TEST);
    }

    @AfterEach
    public void clearFile() {
        try {
            new FileWriter(PATH_TO_DATA_TEST, false).close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void shouldReturnEmptyListTasksAfterSerialization() {
        TasksManager tasksManagerFromFile = FileBackedTasksManager
                .loadFromFile(new File(PATH_TO_DATA_TEST));
        assertEquals(0, tasksManagerFromFile.getTasks().size(), "Ожидалось, что список задач," +
                " загруженный из пустого файла будет пустым, однако он не пуст.");
    }

    @Test
    public void shouldReturnListTasksAfterSerialization() {

        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);

        taskManager.createTask(task1);

        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.createSubtask(subtask3);

        TasksManager tasksManagerFromFile = FileBackedTasksManager
                .loadFromFile(new File(PATH_TO_DATA_TEST));
        assertEquals(2, tasksManagerFromFile.getEpics().size()
                , "Ожидалось, что загрузятся два эпика, однако эпики загружены из файла некорректно.");
        assertEquals(1, tasksManagerFromFile.getTasks().size()
                , "Ожидалось, что загрузится одна задача, однако задачи загружены из файла некорректно.");
        assertEquals(3, tasksManagerFromFile.getSubtasks().size()
                , "Ожидалось, что загрузятся три подзадачи, однако эпики загружены из файла некорректно.");

        List<Task> tasksFromFile = tasksManagerFromFile.getTasks();
        List<Subtask> subtasksFromFile = tasksManagerFromFile.getSubtasks();
        List<Epic> epicsFromFile = tasksManagerFromFile.getEpics();

        List<Task> tasks = List.of(task1);
        List<Subtask> subtasks = List.of(subtask1, subtask2, subtask3);
        List<Epic> epics = List.of(epic1, epic2);

        assertEquals(tasks, tasksFromFile, "Ожидалось, что задачи будут совпадать с исходными.");
        assertEquals(subtasks, subtasksFromFile, "Ожидалось, что подзадачи будут совпадать с исходными.");
        assertEquals(epics, epicsFromFile, "Ожидалось, что эпики будут совпадать с исходными.");
    }

    @Test
    public void shouldReturnEpicWithoutSubtasksAfterSerialization() {

        taskManager.createEpic(epic1);

        TasksManager tasksManagerFromFile = FileBackedTasksManager
                .loadFromFile(new File(PATH_TO_DATA_TEST));

        assertEquals(taskManager.getEpic(1), tasksManagerFromFile.getEpic(1), "Ожидалось" +
                ", что будет создан один эпик без подзадач и считан корректно с файла.");
    }

    @Test
    public void shouldReturn1345History() {

        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);

        taskManager.createTask(task1);

        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.createSubtask(subtask3);

        taskManager.getEpic(1);
        taskManager.getTask(3);
        taskManager.getSubtask(4);
        taskManager.getSubtask(5);

        TasksManager tasksManagerFromFile = FileBackedTasksManager
                .loadFromFile(new File(PATH_TO_DATA_TEST));

        assertEquals(List.of(1, 3, 4, 5), tasksManagerFromFile.getIdHistory(), "Ожидалось" +
                ", что вернет корректную историю в виде 1, 3, 4, 5.");
    }

    @Test
    public void shouldReturnEmptyHistory() {

        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);

        taskManager.createTask(task1);

        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.createSubtask(subtask3);

        TasksManager tasksManagerFromFile = FileBackedTasksManager
                .loadFromFile(new File(PATH_TO_DATA_TEST));

        assertEquals(Collections.emptyList(), tasksManagerFromFile.getIdHistory(), "Ожидалось" +
                ", что история будет пустой.");
    }

    @Test
    public void shouldThrowSaveExceptionIfIOException() {
        FileBackedTasksManager tasksManager =
                (FileBackedTasksManager) Managers.getFileBackedTasksManager("resources");
        assertThrows(ManagerSaveException.class, tasksManager::save, "Ожидалось исключение" +
                " при прочтении несуществующег файла или директории.");
    }
}

