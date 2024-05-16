package util;

import service.FileBackedTasksManager;
import service.HistoryManager;
import service.InMemoryHistoryManager;
import service.InMemoryTasksManager;

import java.io.File;

public class Managers {
    private Managers() {
    }

    public static InMemoryTasksManager getInMemoryTasksManager() {
        return new InMemoryTasksManager();
    }

    public static FileBackedTasksManager getFileBackedTasksManager() {
        return new FileBackedTasksManager(new File("resources/task.csv"));
    }

    public static FileBackedTasksManager getFileBackedTasksManager(String path) {
        return new FileBackedTasksManager(new File(path));
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

}
