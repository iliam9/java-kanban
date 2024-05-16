package serviceTest;

import org.junit.jupiter.api.BeforeEach;
import service.InMemoryTasksManager;
import util.Managers;

public class InMemoryTasksManagerTest extends TasksManagerTest<InMemoryTasksManager> {

    @BeforeEach
    public void initManager() {
        taskManager = Managers.getInMemoryTasksManager();
    }
}

