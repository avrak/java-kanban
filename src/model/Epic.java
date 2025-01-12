package model;

import java.util.ArrayList;

public class Epic extends Task {

    private ArrayList<Integer> subTasksIds;

    public Epic(TaskType type, String name, String description) {
        super(type, name, description);
        subTasksIds = new ArrayList<>();
    }

    public Epic(TaskType type, String name, String description, int epicId, String status) {
        super(type, name, description, epicId, status);
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
}
