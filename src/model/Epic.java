package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;

public class Epic extends Task {

    private ArrayList<Integer> subTasksIds;
    private LocalDateTime endDateTime;

    public Epic(TaskType type, String name, String description) {
        super(type, name, description, null, null);
        subTasksIds = new ArrayList<>();
    }

    public Epic(TaskType type, String name, String description, int epicId, String status) {
        super(type, name, description, epicId, status, null, null);
        subTasksIds = new ArrayList<>();
    }

    public ArrayList<Integer> getSubTasksIds() {
        return subTasksIds;
    }

    public void addSubTaskId(int subTaskId) {
        subTasksIds.add(subTaskId);
    }

    public void deleteSubTask(Integer subTaskId) {
        subTasksIds.remove(subTaskId);
    }

    public void deleteAllSubTasksIds() {
        subTasksIds.clear();
    }

    public void setEndDateTime(LocalDateTime endDateTime) {
        this.endDateTime = endDateTime;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endDateTime;
    }

    @Override
    public String toString() {
        return super.toString().substring(0, super.toString().indexOf(", duration")) + "}";
    }

    @Override
    public String toFileString() {
        ArrayList<String> details = new ArrayList<>(Arrays.asList(super.toFileString().split(",")));
        return String.join(",", details);
    }
}
