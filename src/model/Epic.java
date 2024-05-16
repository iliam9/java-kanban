package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {

    private final List<Integer> subtasks;

    public Epic(String title, String description) {
        super(title, description, Status.NEW, LocalDateTime.now().withNano(0), Duration.ofMinutes(0));
        subtasks = new ArrayList<>();
    }

    public Epic(int id, String title, String description) {
        super(id, title, description, Status.NEW, LocalDateTime.now().withNano(0), Duration.ofMinutes(0));
        subtasks = new ArrayList<>();
    }

    public Epic(int id, String title, String description, LocalDateTime startTime, Duration duration) {
        super(id, title, description, Status.NEW, startTime, duration);
        subtasks = new ArrayList<>();
    }

    public void addSubtask(int id) {
        subtasks.add(id);
    }

    public List<Integer> getSubtasksId() {
        return subtasks;
    }

    public void removeSubtask(Integer id) {
        subtasks.remove(id);
    }

    public void removeAllSubtasks() {
        subtasks.clear();
    }

    @Override
    public String toString() {
        return "Epic{" +
                "subtasks=" + subtasks +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", startTime=" + startTime +
                ", duration=" + duration +
                ", status=" + status +
                '}';
    }
}
