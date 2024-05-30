package modelTest;

import model.Epic;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EpicTest {

    @Test
    public void shouldReturnEmptyListIdsOfSubtasks() {
        Epic epic = new Epic("epic1", "description1");
        assertEquals(Collections.emptyList(), epic.getSubtasksId()
                , "Ожидалось получить пустой список под задач при создании нового эпика.");
    }

    @Test
    public void shouldGet2SubtasksAfterAdd() {
        Epic epic = new Epic("epic1", "description1");

        epic.addSubtask(2);
        epic.addSubtask(3);

        List<Integer> expectedSubtasks = List.of(2, 3);
        assertEquals(expectedSubtasks, epic.getSubtasksId()
                , "Ожидалось получить список из двух подзадач при добавлении их в эпик.");
    }

    @Test
    public void shouldGetSubtasksAfterAddAndRemove() {
        Epic epic = new Epic("epic1", "description1");

        epic.addSubtask(1);
        epic.addSubtask(2);
        epic.addSubtask(3);
        epic.addSubtask(4);

        epic.removeSubtask(1);
        epic.removeSubtask(2);

        List<Integer> expectedSubtasks = List.of(3, 4);
        assertEquals(expectedSubtasks, epic.getSubtasksId(), "Ожидалось " +
                "получить корректный список из подзадач при добавлении их в эпик и последующем удалении.");
    }

    @Test
    public void shouldReturnEmptyListOfSubtasksAfterDeleteAll() {
        Epic epic = new Epic("epic1", "description1");

        epic.addSubtask(1);
        epic.addSubtask(2);
        epic.addSubtask(3);
        epic.addSubtask(4);

        epic.removeAllSubtasks();

        assertEquals(Collections.emptyList(), epic.getSubtasksId()
                , "Ожидалось получить пустой список подзадач после их полного удаления из эпика.");
    }

}
