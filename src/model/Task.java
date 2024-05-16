package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task {
    protected String title;
    protected String description;
    protected int id;
    protected LocalDateTime startTime;
    protected LocalDateTime endTime;
    protected Duration duration;
    protected Status status;

    protected LocalDateTime calculateEndTime() {
        return startTime.plus(duration);
    }

    public Task(String title, String description, Status status, LocalDateTime startTime, Duration duration) {
        this.id = 0;
        this.title = title;
        this.description = description;
        this.startTime = startTime;
        this.duration = duration;
        this.endTime = calculateEndTime();
        this.status = status;
    }

    public Task(int id, String title, String description, Status status, LocalDateTime startTime, Duration duration) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.startTime = startTime;
        this.duration = duration;
        this.endTime = calculateEndTime();
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime localDateTime) {
        this.startTime = localDateTime;
        endTime = startTime.plus(duration);
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
        this.endTime = calculateEndTime();
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return this.title.equals(task.title)
                && this.description.equals(task.description)
                && this.status.equals(task.status)
                && this.startTime.equals(task.startTime)
                && this.duration.equals(task.duration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, description, status, startTime, duration);
    }

    @Override
    public String toString() {
        return "Task{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", startTime=" + startTime +
                ", duration=" + duration +
                ", status=" + status +
                '}';
    }
}
