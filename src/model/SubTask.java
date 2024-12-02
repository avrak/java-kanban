package model;

public class SubTask extends Task {
    private int epicId;

    public SubTask (TaskType type, int epicId, String name, String description) {
        super(type, name, description);
        this.epicId = epicId;
    }

    public Integer getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return getType()
                + "{"
                + "taskId=" + getTaskId()
                + ", epicId=" + epicId
                + ", name='" + getName() + '\''
                + ", description='" + getDescription() + '\''
                + ", status=" + getStatus()
                + '}';
    }

}
