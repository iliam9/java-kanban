package serviceTest;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.shadow.com.univocity.parsers.common.DataValidationException;
import service.TasksManager;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

abstract class TasksManagerTest<T extends TasksManager> {

    T taskManager;

    Task task1;
    Task task2;
    Task task3;

    Epic epic1;
    Epic epic2;
    Epic epic3;

    Subtask subtask1;
    Subtask subtask2;
    Subtask subtask3;

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

    @Test
    public void shouldCreateTask() {

        taskManager.createTask(task1);

        final Task savedTask = taskManager.getTask(1);
        assertNotNull(savedTask, "Ожидалось, что задача будет не null.");
        assertEquals(task1, savedTask, "Ожидалось, что задачи будут равны.");

        final List<Task> tasks = taskManager.getTasks();

        assertNotNull(tasks, "Ожидалось, что список задач будет непустным.");
        assertEquals(1, tasks.size(), "Ожидалось получить количество, равное 1, задач.");
        assertEquals(task1, tasks.get(0), "Ожидалось, что полученная и сохраненная задачи будут равны.");
    }

    @Test
    public void shouldCreateSomeTasksWithId123() {

        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createTask(task3);

        final List<Task> savedTasks = taskManager.getTasks();

        assertEquals(3, savedTasks.size(), "Ожидалось, что размер списка задач будет равен 3.");
        assertNotNull(savedTasks.get(0), "Ожидалось, что задача будет не null.");
        assertNotNull(savedTasks.get(1), "Ожидалось, что задача будет не null.");
        assertNotNull(savedTasks.get(2), "Ожидалось, что задача будет не null.");

        assertEquals(task1, savedTasks.get(0), "Ожидалось, что изначальная и загруженная задачи будут равны.");
        assertEquals(task2, savedTasks.get(1), "Ожидалось, что изначальная и загруженная задачи будут равны.");
        assertEquals(task3, savedTasks.get(2), "Ожидалось, что изначальная и загруженная задачи будут равны.");

        assertEquals(1, task1.getId(), "Ожидалось, что индентификатор у 1-ой задачи будет 1.");
        assertEquals(2, task2.getId(), "Ожидалось, что индентификатор у 2-ой задачи будет 2.");
        assertEquals(3, task3.getId(), "Ожидалось, что индентификатор у 3-ей задачи будет 3.");
    }

    @Test
    public void shouldCreateEpic() {

        taskManager.createEpic(epic1);

        final Epic savedEpic = taskManager.getEpic(1);
        assertNotNull(savedEpic, "Ожидалось, что эпик будет не null.");
        assertEquals(epic1, savedEpic, "Ожидалось, что эпики совпадут.");

        final List<Epic> epics = taskManager.getEpics();

        assertNotNull(epics, "Ожидалось, что список эпиков будет не null.");
        assertEquals(1, epics.size(), "Ожидалось получить количество, равное 1, эпиков.");
        assertEquals(epic1, epics.get(0), "Ожидалось, что полученный и сохранененный эпики будут равны.");
    }

    @Test
    public void shouldCreateSomeEpicsWithId123() {

        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        taskManager.createEpic(epic3);

        final List<Epic> savedEpics = taskManager.getEpics();

        assertNotNull(savedEpics.get(0), "Ожидалось, что эпик будет не null.");
        assertNotNull(savedEpics.get(1), "Ожидалось, что эпик будет не null.");
        assertNotNull(savedEpics.get(2), "Ожидалось, что эпик будет не null.");

        assertEquals(epic1, savedEpics.get(0), "Ожидалось, что изначальный и загруженный эпики совпадут.");
        assertEquals(epic2, savedEpics.get(1), "Ожидалось, что изначальный и загруженный эпики совпадут.");
        assertEquals(epic3, savedEpics.get(2), "Ожидалось, что изначальный и загруженный эпики совпадут.");

        assertEquals(1, epic1.getId(), "Ожидалось, что индентификатор у 1-ого эпика будет 1.");
        assertEquals(2, epic2.getId(), "Ожидалось, что индентификатор у 2-ого эпика будет 2.");
        assertEquals(3, epic3.getId(), "Ожидалось, что индентификатор у 3-ого эпика будет 3.");
    }

    @Test
    public void shouldCreateSubtask() {

        taskManager.createEpic(epic1);
        taskManager.createSubtask(subtask1);

        final Subtask savedSubtask = taskManager.getSubtask(2);
        assertNotNull(savedSubtask, "Ожидалось, что подзадача будет не null.");
        assertEquals(subtask1, savedSubtask, "Ожидалось, что изначальная и загруженная подзадачи совпадут.");

        final List<Subtask> subtasks = taskManager.getSubtasks();

        assertNotNull(subtasks, "Ожидалось, что список подазадач будет непустым");
        assertEquals(1, subtasks.size(), "Ожидалось, что будет одна подзадача в списке" +
                " полученных подазадач.");
        assertEquals(subtask1, subtasks.get(0), "Ожидалось, что изначальная и загруженная подзадачи совпадут.");
    }

    @Test
    public void shouldCreateSomeSubtasksWithId234() {

        taskManager.createEpic(epic1);

        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.createSubtask(subtask3);

        final List<Subtask> savedSubtasks = taskManager.getSubtasks();

        assertNotNull(savedSubtasks.get(0), "Ожидалось, что подзадача будет не null.");
        assertNotNull(savedSubtasks.get(1), "Ожидалось, что подзадача будет не null.");
        assertNotNull(savedSubtasks.get(2), "Ожидалось, что подзадача будет не null.");

        assertEquals(subtask1, savedSubtasks.get(0), "Ожидалось, что изначальная и загруженная подзадачи совпадут.");
        assertEquals(subtask2, savedSubtasks.get(1), "Ожидалось, что изначальная и загруженная подзадачи совпадут.");
        assertEquals(subtask3, savedSubtasks.get(2), "Ожидалось, что изначальная и загруженная подзадачи совпадут.");

        assertEquals(2, subtask1.getId(), "Ожидалось, что индентификатор у 1-ой подзадачи будет 1.");
        assertEquals(3, subtask2.getId(), "Ожидалось, что индентификатор у 2-ой подзадачи будет 2.");
        assertEquals(4, subtask3.getId(), "Ожидалось, что индентификатор у 3-ей подзадачи будет 3.");
    }

    @Test
    public void shouldUpdateTask() {
        taskManager.createTask(task1);

        task1.setTitle("newTask");
        task1.setDescription("newDescription");
        task1.setStatus(Status.DONE);

        taskManager.updateTask(task1);

        Task savedTask = taskManager.getTask(1);
        assertEquals(task1, savedTask, "Ожидалось, при обновлении задача обновится.");
    }

    @Test
    public void shouldUpdateSubtask() {

        taskManager.createEpic(epic1);
        taskManager.createSubtask(subtask1);

        subtask1.setTitle("newTask");
        subtask1.setDescription("newDescription");
        subtask1.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask1);

        Task savedSubtask = taskManager.getSubtask(2);
        assertEquals(subtask1, savedSubtask, "Ожидалось, при обновлении подзадача обновится.");
    }

    @Test
    public void shouldUpdateEpic() {
        taskManager.createEpic(epic1);

        epic1.setTitle("newTask");
        epic1.setDescription("newDescription");

        taskManager.updateEpic(epic1);
        Epic savedEpic = taskManager.getEpic(1);
        assertEquals(epic1, savedEpic, "Ожидалось, при обновлении эпик обновится.");
    }

    @Test
    public void shouldReturnSubtasksFromEpic() {

        taskManager.createEpic(epic1);

        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.createSubtask(subtask3);

        final List<Subtask> savedSubtasks = taskManager.getSubtasksFromEpic(1);

        assertNotNull(savedSubtasks.get(0), "Ожидалось, что подзадача, полученная из эпика будет не null.");
        assertNotNull(savedSubtasks.get(1), "Ожидалось, что подзадача, полученная из эпика будет не null.");
        assertNotNull(savedSubtasks.get(2), "Ожидалось, что подзадача, полученная из эпика будет не null.");

        assertEquals(subtask1, savedSubtasks.get(0), "Ожидалось, что изначальная и " +
                "загруженная из эпика подзадачи совпадут.");
        assertEquals(subtask2, savedSubtasks.get(1), "Ожидалось, что изначальная и " +
                "загруженная из эпика подзадачи совпадут.");
        assertEquals(subtask3, savedSubtasks.get(2), "Ожидалось, что изначальная и " +
                "загруженная из эпика подзадачи совпадут.");

        assertEquals(2, subtask1.getId(), "Ожидалось, что индентификатор у 1-ой подзадачи" +
                ", загруженной из эпика, будет 2.");
        assertEquals(3, subtask2.getId(), "Ожидалось, что индентификатор у 2-ой подзадачи" +
                ", загруженной из эпика, будет 3.");
        assertEquals(4, subtask3.getId(), "Ожидалось, что индентификатор у 3-ей подзадачи" +
                ", загруженной из эпика, будет 3.");
    }

    @Test
    public void shouldReturnNullGetSubtasksIfEpicDoesNotExist() {
        assertNull(taskManager.getSubtasksFromEpic(50)
                , "Ожидался null при получении несуществующей подзадачи из эпика");
    }

    @Test
    public void shouldReturnPrioritizedTasks() {
        task1.setStartTime(LocalDateTime.parse("2023-01-01T00:00"));
        task2.setStartTime(LocalDateTime.parse("2022-01-01T00:00"));
        task3.setStartTime(LocalDateTime.parse("2022-01-01T10:00"));

        subtask1.setStartTime(LocalDateTime.parse("2022-10-01T00:00"));
        subtask2.setStartTime(LocalDateTime.parse("2025-01-01T00:00"));
        subtask3.setStartTime(LocalDateTime.parse("2021-01-01T00:01"));

        taskManager.createEpic(epic1);

        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createTask(task3);

        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.createSubtask(subtask3);

        List<Task> expectedPrioritizedTasks = List.of(subtask3, task2, task3, subtask1, task1, subtask2);
        List<Task> actualPrioritizedTasks = taskManager.getPrioritizedTasks();

        assertEquals(expectedPrioritizedTasks, actualPrioritizedTasks,
                "Ожидалось, что время изначальных и загруженный задач будет совпадать.");
    }

    @Test
    public void shouldReturnEmptyPrioritizedTasks() {
        assertEquals(Collections.emptyList(), taskManager.getPrioritizedTasks()
                , "Ожидалось, что список отсортированнных по приоритету задач будет пуст.");

        task1.setStartTime(LocalDateTime.parse("2023-01-01T00:00"));
        task2.setStartTime(LocalDateTime.parse("2022-01-01T00:00"));
        task3.setStartTime(LocalDateTime.parse("2022-01-01T10:00"));

        subtask1.setStartTime(LocalDateTime.parse("2022-10-01T00:00"));
        subtask2.setStartTime(LocalDateTime.parse("2025-01-01T00:00"));
        subtask3.setStartTime(LocalDateTime.parse("2021-01-01T00:01"));

        taskManager.createEpic(epic1);

        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createTask(task3);

        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.createSubtask(subtask3);

        taskManager.deleteAllTasks();
        taskManager.deleteAllEpics();

        assertEquals(Collections.emptyList(), taskManager.getPrioritizedTasks()
                , "Ожидалось, что список отсортированнных по приоритету задач будет пуст.");
    }

    @Test
    public void shouldRemoveTask() {
        taskManager.createTask(task1);
        taskManager.deleteTask(1);

        assertEquals(0, taskManager.getTasks().size());
    }

    @Test
    public void shouldRemoveAllTasks() {
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createTask(task3);

        taskManager.deleteAllTasks();

        assertEquals(0, taskManager.getTasks().size()
                , "Ожидалось получить пустой список задач после удаления.");
    }

    @Test
    public void shouldRemoveSubtask() {
        taskManager.createEpic(epic1);

        taskManager.createSubtask(subtask1);

        taskManager.deleteAllSubtasks();
        assertTrue(epic1.getSubtasksId().isEmpty());
    }

    @Test
    public void shouldRemoveAllSubtasks() {
        taskManager.createEpic(epic1);

        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.createSubtask(subtask3);

        taskManager.deleteAllSubtasks();

        assertEquals(0, taskManager.getSubtasks().size(),
                "Ожидалось получить пустой список подзадач после удаления.");
    }

    @Test
    public void shouldRemoveEpic() {
        taskManager.createEpic(epic1);

        taskManager.deleteEpic(1);
        assertEquals(0, taskManager.getEpics().size()
                , "Ожидалось получить пустой список эпиков после удаления.");
    }

    @Test
    public void shouldRemoveEpicAndItsSubtasks() {
        taskManager.createEpic(epic1);
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.createSubtask(subtask3);

        taskManager.deleteEpic(1);
        assertEquals(0, taskManager.getEpics().size()
                , "Ожидалось получить пустой список эпиков после удаления.");
    }

    @Test
    public void shouldRemoveAllEpics() {
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        taskManager.createEpic(epic3);

        subtask1.setEpicId(1);
        subtask2.setEpicId(2);
        subtask3.setEpicId(3);

        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.createSubtask(subtask3);

        taskManager.deleteAllEpics();

        assertEquals(0, taskManager.getSubtasks().size(), "Ожидалось, что список подзадач будет пустым.");
        assertEquals(0, taskManager.getEpics().size(), "Ожидалось, что список эпиков будет пустым.");
    }

    @Test
    public void shouldReturnNewStatusWhenEpicIsEmpty() {
        taskManager.createEpic(epic1);
        assertEquals(Status.NEW, epic1.getStatus(), "Ожидалось, что статус пустого эпика будет 'NEW'");

        taskManager.createSubtask(subtask1);
        assertEquals(Status.NEW, epic1.getStatus(), "Ожидалось," +
                " что статус эпика с подзадачами со статусом 'NEW' будет 'NEW'");

        taskManager.deleteSubtask(1);
        assertEquals(Status.NEW, epic1.getStatus(), "Ожидалось," +
                " что статус у пустого эпика будет после удаления подзадачи статус 'NEW'");
    }

    @Test
    public void shouldReturnNewStatusIfAllSubtasksIsNew() {
        subtask1.setStatus(Status.NEW);
        subtask2.setStatus(Status.NEW);
        subtask3.setStatus(Status.NEW);

        taskManager.createEpic(epic1);
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.createSubtask(subtask3);

        Status epicStatus = taskManager.getEpic(1).getStatus();
        assertEquals(Status.NEW, epicStatus, "Ожидалось," +
                " что статус эпика с подзадачами со статусом 'NEW' будет 'NEW'");
    }

    @Test
    public void shouldReturnDoneStatusIfAllSubtasksIsDone() {
        subtask1.setStatus(Status.DONE);
        subtask2.setStatus(Status.DONE);
        subtask3.setStatus(Status.DONE);

        taskManager.createEpic(epic1);
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.createSubtask(subtask3);

        Status epicStatus = taskManager.getEpic(1).getStatus();
        assertEquals(Status.DONE, epicStatus, "Ожидалось," +
                " что статус эпика с подзадачами со статусом 'DONE' будет 'DONE'");
    }

    @Test
    public void shouldInProgressStatusIfAllSubtasksIsNewAndDone() {
        subtask1.setStatus(Status.NEW);
        subtask2.setStatus(Status.DONE);
        subtask3.setStatus(Status.NEW);

        taskManager.createEpic(epic1);
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.createSubtask(subtask3);

        Status epicStatus = taskManager.getEpic(1).getStatus();
        assertEquals(Status.IN_PROGRESS, epicStatus, "Ожидалось," +
                " что статус эпика с подзадачами с разными статусами будет 'IN_PROGRESS'");
    }

    @Test
    public void shouldInProgressStatusIfAllSubtasksIsInProgress() {
        subtask1.setStatus(Status.IN_PROGRESS);
        subtask2.setStatus(Status.IN_PROGRESS);
        subtask3.setStatus(Status.IN_PROGRESS);

        taskManager.createEpic(epic1);
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.createSubtask(subtask3);

        Status epicStatus = taskManager.getEpic(1).getStatus();
        assertEquals(Status.IN_PROGRESS, epicStatus, "Ожидалось," +
                " что статус эпика с подзадачами со статусами 'IN_PROGRESS' будет 'IN_PROGRESS'");
    }

    @Test
    public void shouldCalculateEpicTimeWithOneSubtask() {
        subtask1.setStartTime(LocalDateTime.of(2023, 6, 1, 0, 0));
        subtask1.setDuration(Duration.ofMinutes(120));
        LocalDateTime endTime = subtask1.getEndTime();
        LocalDateTime startTime = subtask1.getStartTime();

        taskManager.createEpic(epic1);
        taskManager.createSubtask(subtask1);

        assertEquals(startTime, epic1.getStartTime()
                , "Ожидалось" +
                        ", что время старта эпика будет совпадать со временем старта его единственной подзадачи");
        assertEquals(endTime, epic1.getEndTime()
                , "Ожидалось" +
                        ", что время конца эпика будет совпадать со временем конца его единственной подзадачи");
    }

    @Test
    public void shouldReturnRightEpicDuration() {
        subtask1.setStartTime(LocalDateTime.of(2023, 6, 1, 0, 0));
        subtask1.setDuration(Duration.ofMinutes(120));

        subtask2.setStartTime(LocalDateTime.of(2023, 6, 1, 3, 0));
        subtask2.setDuration(Duration.ofMinutes(60));

        subtask3.setStartTime(LocalDateTime.of(2023, 5, 1, 3, 0));
        subtask3.setDuration(Duration.ofMinutes(0));

        taskManager.createEpic(epic1);
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.createSubtask(subtask3);

        assertEquals(Duration.ofHours(3), epic1.getDuration()
                , "Ожидалось" +
                        ", что время эпика будет равно сумме длительности подзадач");
    }

    @Test
    public void shouldReturnEmptyHistory() {
        assertEquals(0, taskManager.getHistory().size()
                , "Ожидалась пустая история при создании нового менеджера.");

        taskManager.createEpic(epic1);
        taskManager.createTask(task1);
        taskManager.createSubtask(subtask1);

        taskManager.getEpic(1);
        taskManager.getTask(2);
        taskManager.getSubtask(3);

        taskManager.deleteEpic(1);
        taskManager.deleteTask(2);
        taskManager.deleteSubtask(3);

        assertEquals(0, taskManager.getHistory().size()
                , "Ожидалась пустая история после удаления всех задач.");
    }

    @Test
    public void shouldReturnRightHistoryWhenSomeDeleted() {

        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        taskManager.createEpic(epic3);

        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createTask(task3);

        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.createSubtask(subtask3);

        taskManager.getEpic(1);
        taskManager.getEpic(2);
        taskManager.getEpic(3);
        taskManager.getTask(4);
        taskManager.getSubtask(7);
        taskManager.getSubtask(8);
        taskManager.getSubtask(9);

        assertEquals(List.of(1, 2, 3, 4, 7, 8, 9), taskManager.getIdHistory()
                , "Ожидалось получить корректную историю.");

        taskManager.deleteEpic(1);

        assertEquals(List.of(2, 3, 4), taskManager.getIdHistory()
                , "Ожидалось получить корректную историю после удаления эпика с подзадачами.");

        taskManager.deleteEpic(3);

        assertEquals(List.of(2, 4), taskManager.getIdHistory()
                , "Ожидалось получить корректную историю после удаления эпика без подзадач.");

        taskManager.deleteTask(4);

        assertEquals(List.of(2), taskManager.getIdHistory()
                , "Ожидалось получить корректную историю после удаления задачи.");
    }

    @Test
    public void shouldReturnRightHistoryWhenSomeDubbed() {

        taskManager.createEpic(epic1);
        taskManager.createTask(task1);
        taskManager.createSubtask(subtask1);

        taskManager.getEpic(1);
        taskManager.getTask(2);
        taskManager.getSubtask(3);
        taskManager.getTask(2);
        taskManager.getEpic(1);
        taskManager.getEpic(1);

        assertEquals(List.of(3, 2, 1), taskManager.getIdHistory()
                , "Ожидалось получить корректную историю после дублированного получения задач.");
    }

    @Test
    public void shouldThrowValidationException() {
        task1.setStartTime(LocalDateTime.parse("2021-06-03T13:54"));
        task1.setDuration(Duration.ofMinutes(30));
        task2.setStartTime(LocalDateTime.parse("2021-06-03T13:54"));
        task2.setDuration(Duration.ofMinutes(15));

        assertThrows(DataValidationException.class, () -> {
                    taskManager.createTask(task1);
                    taskManager.createTask(task2);
                }
                , "Ожидалось получить исключение при пересечении времени задач.");

    }

}
