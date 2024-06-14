package serviceTest;


import model.Status;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.HistoryManager;
import service.InMemoryHistoryManager;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class InMemoryHistoryManagerTest {
    Task task1 = new Task(1, "task1", "taskDescription1", Status.NEW,
            LocalDateTime.parse("2021-06-03T13:54"), Duration.ofMinutes(100));
    Task task2 = new Task(2, "task2", "taskDescription2", Status.NEW,
            LocalDateTime.parse("2021-05-03T13:54"), Duration.ofMinutes(100));
    Task task3 = new Task(3, "task3", "taskDescription3", Status.NEW,
            LocalDateTime.parse("2021-06-03T17:54"), Duration.ofMinutes(100));

    HistoryManager manager;

    @BeforeEach
    public void beforeEach() {
        manager = new InMemoryHistoryManager();
    }

    @Test
    public void addToEmptyManager() {
        Task task1 = new Task(1, "task1", "taskDescription1", Status.NEW,
                LocalDateTime.parse("2021-06-03T13:54"), Duration.ofMinutes(100));

        manager.add(task1);

        assertEquals(List.of(task1), manager.getHistory());
        assertNotNull(manager.getHistory());
    }

    @Test
    public void addSeveralTasks() {
        Task task1 = new Task(1, "task1", "taskDescription1", Status.NEW,
                LocalDateTime.parse("2021-06-03T13:54"), Duration.ofMinutes(100));
        Task task2 = new Task(2, "task2", "taskDescription2", Status.NEW,
                LocalDateTime.parse("2021-05-03T13:54"), Duration.ofMinutes(100));

        manager.add(task1);
        manager.add(task2);

        assertEquals(List.of(task1, task2), manager.getHistory());
        assertEquals(2, manager.getHistory().size());
        assertNotNull(manager.getHistory());
    }

    @Test
    public void getHistoryWithRepeatedTasks() {
        manager.add(task1);
        manager.add(task2);
        manager.add(task3);
        manager.add(task1);
        manager.add(task2);
        manager.add(task3);

        List<Task> rightHistory = List.of(task1, task2, task3);

        assertNotNull(manager.getHistory());
        assertEquals(rightHistory, manager.getHistory());
        assertEquals(3, manager.getHistory().size());
    }

    @Test
    public void removeFromBeginning() {

        manager.add(task1);
        manager.add(task2);
        manager.add(task3);

        manager.delete(1);

        assertEquals(List.of(task2, task3), manager.getHistory());
        assertNotNull(manager.getHistory());
        assertEquals(2, manager.getHistory().size());
    }

    @Test
    public void removeFromMiddle() {
        manager.add(task1);
        manager.add(task2);
        manager.add(task3);

        manager.delete(2);

        assertEquals(List.of(task1, task3), manager.getHistory());
        assertNotNull(manager.getHistory());
        assertEquals(2, manager.getHistory().size());
    }

    @Test
    public void removeFromEnd() {
        manager.add(task1);
        manager.add(task2);
        manager.add(task3);

        manager.delete(3);

        assertEquals(List.of(task1, task2), manager.getHistory());
        assertNotNull(manager.getHistory());
        assertEquals(2, manager.getHistory().size());
    }
}

