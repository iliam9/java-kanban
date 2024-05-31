package util;

import service.*;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

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

    public static TasksManager getDefault() {
        try {
            return new HttpTaskManager(new URL("http://localhost:8078/"));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
    public static HttpTaskManager getHttpTaskManager(final int PORT) {
        try {
            return new HttpTaskManager(new URL("http://localhost:" + PORT));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }


}
