package main.java.ru.yandex.practicum.canban.model;

public class TaskId {
    private static int taskId;

    public TaskId() {
    }

    public static int getNewTaskId() {
        return taskId++;
    }
}
