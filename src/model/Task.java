package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Task extends TaskId {
    public static final String DATE_TIME_FORMAT = "dd-MM-yyyy HH:mm";
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);

    private int taskId;
    private TaskType type;
    private String name;
    private String description;
    private TaskStatus status;
    private Duration duration;
    private LocalDateTime startTime;

    public Task(TaskType type, String name, String description, Duration duration, LocalDateTime startTime) {
        this.taskId = getNewTaskId();
        this.type = type;
        this.name = name;
        this.description = description;
        this.status = TaskStatus.NEW;
        this.duration = duration;
        this.startTime = startTime;
    }

    public Task(TaskType type, String name, String description, int taskId, String status, Duration duration, LocalDateTime startTime) {
        this.taskId = taskId;
        if (taskId >= getCurrentTaskId()) {
            setNewTaskId(taskId + 1);
        }
        this.type = type;
        this.name = name;
        this.description = description;
        this.status = TaskStatus.valueOf(status);
        this.duration = duration;
        this.startTime = startTime;
    }


    public TaskStatus getStatus() {
        return status;
    }

    public int getTaskId() {
        return taskId;
    }

    public TaskType getType() {
        return type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public Duration getDuration() {
        return duration;
    }

    private String getDurationString() {
        String durationString = "";

        if (duration != null) {
            durationString = Long.valueOf(duration.toMinutes()).toString();
        }
        return durationString;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    private String getStartTimeString() {
        String startTimeString = "";

        if (startTime != null) {
            startTimeString = startTime.format(DATE_TIME_FORMATTER);
        }
        return startTimeString;
    }

    public LocalDateTime getEndTime() {
        return startTime.plus(duration);
    }

    @Override
    public String toString() {
        return type
                + "{"
                + "taskId=" + taskId
                + ", name='" + name + '\''
                + ", description='" + description + '\''
                + ", status=" + status
                + ", duration=" + getDurationString()
                + ", startTime=" + getStartTimeString()
                + '}';
    }

    public String toFileString() {
        return String.format("%s,%s,%s,%s,%s,%s,%s", type, taskId, name, description, status, getDurationString(), getStartTimeString());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return taskId == task.taskId && name.equals(task.name) && description.equals(task.description) && status == task.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskId, name, description, status, duration, startTime);
    }


}
