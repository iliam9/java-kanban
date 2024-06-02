package util;

import exceptions.GetHttpTaskManagerException;
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
            throw new GetHttpTaskManagerException("Unable to get HttpTaskManager.");
        }
    }

    public static HttpTaskManager getHttpTaskManager(final int port) {
        try {
            return new HttpTaskManager(new URL("http://localhost:" + port));
        } catch (MalformedURLException e) {
            throw new GetHttpTaskManagerException("Unable to get HttpTaskManager.");
        }
    }


}
