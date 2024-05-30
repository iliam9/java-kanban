package service;

import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class InMemoryHistoryManager implements HistoryManager {
    private final CustomLinkedList history = new CustomLinkedList();

    @Override
    public List<Task> getHistory() {
        return history.getTasks();
    }

    @Override
    public List<Integer> getIdHistory() {
        return getHistory()
                .stream()
                .map(Task::getId)
                .collect(Collectors.toList());
    }

    @Override
    public void add(Task task) {
        history.linkLast(task);
    }

    @Override
    public void delete(int id) {
        history.removeNode(id);
    }

    private static class CustomLinkedList {
        public static class Node<T> {
            public T task;
            public Node<T> next;
            public Node<T> prev;

            public Node(Node<T> prev, T task, Node<T> next) {
                this.task = task;
                this.next = next;
                this.prev = prev;
            }
        }

        private Node<Task> first;
        private Node<Task> last;

        private final Map<Integer, CustomLinkedList.Node<Task>> nodeMap = new HashMap<>();

        public void linkLast(Task task) {
            if (task == null) {
                return;
            } else {
                removeNode(nodeMap.get(task.getId()));
            }

            final Node<Task> oldLast = last;
            final Node<Task> newNode = new Node<>(oldLast, task, null);
            last = newNode;
            if (oldLast == null) {
                first = newNode;
            } else {
                oldLast.next = newNode;
            }
            nodeMap.put(task.getId(), newNode);
        }

        private void removeNode(int id) {
            removeNode(nodeMap.remove(id));
        }

        private void removeNode(Node<Task> node) {
            if (node == null) {
                return;
            }
            final Node<Task> prev = node.prev;
            final Node<Task> next = node.next;
            if (prev == null) {
                first = next;
            } else {
                prev.next = next;
                node.prev = null;
            }
            if (next == null) {
                last = prev;
            } else {
                next.prev = prev;
                node.next = null;
            }
            node.task = null;
        }

        private List<Task> getTasks() {
            List<Task> tasks = new ArrayList<>();
            for (Node<Task> node = first; node != null; node = node.next) {
                tasks.add(node.task);
            }
            return tasks;
        }
    }
}