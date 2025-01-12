package model;

import java.time.Duration;
import java.time.LocalDateTime;

public class SubTask extends Task {
    private int epicId;

     public SubTask(TaskType type, Epic epic, String name, String description, Duration duration, LocalDateTime startTime) {
        super(type, name, description, duration, startTime);
        this.epicId = epic.getTaskId();
        if (epic.getEndTime() == null || epic.getEndTime().isAfter(startTime.plus(duration))) {
            epic.setEndDateTime(startTime.plus(duration));
        }
    }

    public SubTask(TaskType type, int epicId, String name, String description, int subTaskId, String status, Duration duration, LocalDateTime startTime) {
        super(type, name, description, subTaskId, status, duration, startTime);
        this.epicId = epicId;
    }

    public Integer getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return getType() + "{epicId=" + epicId + ", " + super.toString();
    }

    @Override
    public String toFileString() {
        return String.format("%s,%s", super.toFileString(), epicId);
    }

}
