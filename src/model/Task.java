package model;

import java.util.Objects;

public class Task extends TaskId {

    private int taskId;
    private TaskType type;
    private String name;
    private String description;
    private TaskStatus status;

    public Task(TaskType type, String name, String description) {
        this.taskId = getNewTaskId();
        this.type = type;
        this.name = name;
        this.description = description;
        this.status = TaskStatus.NEW;
    }

    public Task(TaskType type, String name, String description, int TaskId, String status) {
        this.taskId = TaskId;
        this.type = type;
        this.name = name;
        this.description = description;
        this.status = TaskStatus.valueOf(status);
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

    protected String getName() {
        return name;
    }

    protected String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return type
                + "{"
                + "taskId=" + taskId
                + ", name='" + name + '\''
                + ", description='" + description + '\''
                + ", status=" + status
                + '}';
    }

    public String toFileString() {
        return String.format("%s,%s,%s,%s,%s", type, taskId, name, description, status);
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
        return Objects.hash(taskId, name, description, status);
    }


}
