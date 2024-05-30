package model;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {

    private int epicId;

    public Subtask(String title, String description, Status status, LocalDateTime localDateTime, Duration duration, int epicId) {
        super(title, description, status, localDateTime, duration);
        this.epicId = epicId;
    }

    public Subtask(int id, String title, String description, Status status, LocalDateTime localDateTime, Duration duration, int epicId) {
        super(id, title, description, status, localDateTime, duration);
        this.epicId = epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "epicId=" + epicId +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", startTime=" + startTime +
                ", duration=" + duration +
                ", status=" + status +
                '}';
    }
}

