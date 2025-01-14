package model;

public class TaskId {
    private static int taskId;

    public TaskId() {
    }

    public static int getNewTaskId() {
        return taskId++;
    }

    public static int getCurrentTaskId() {
        return taskId;
    }

    public  void setNewTaskId(int taskId) {
        TaskId.taskId = taskId;
    }
}