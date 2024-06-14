package serviceTest;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import service.HttpTaskManager;
import util.Managers;

public class HttpTaskManagerTest extends TasksManagerTest<HttpTaskManager> {
    @BeforeEach
    public void initManager() {
        taskManager = Managers.getHttpTaskManager(8078);
    }

    @AfterAll
    static public void stopServer() {

    }
}
