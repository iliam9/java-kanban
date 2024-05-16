package service;

import additionalStructures.CustomLinkedList;
import additionalStructures.Node;
import model.Task;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;


public class InMemoryHistoryManager implements HistoryManager {
    private final CustomLinkedList<Task> historyTasks;

    private final Map<Integer, Node<Task>> indicesOfNodes;

    public InMemoryHistoryManager() {
        historyTasks = new CustomLinkedList<>();
        indicesOfNodes = new HashMap<>();
    }

    @Override
    public void add(Task task) {

        final int id = task.getId();
        if (Objects.nonNull(indicesOfNodes.get(id))) {
            delete(id);
        }

        final Node<Task> node = historyTasks.linkLast(task);
        indicesOfNodes.put(id, node);
    }

    @Override
    public void delete(int id) {
        final Node<Task> node = indicesOfNodes.remove(id);

        if (Objects.nonNull(node))
            historyTasks.delete(node);
    }

    @Override
    public List<Task> getHistory() {
        return historyTasks.getList();
    }

    @Override
    public List<Integer> getIdHistory() {
        return historyTasks.getList()
                .stream()
                .map(Task::getId)
                .collect(Collectors.toList());
    }
}

