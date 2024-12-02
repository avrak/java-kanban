package model;

public class Node {
    private final Task task;
    private Integer prevId;
    private Integer nextId;

    public Node(Task task, Integer prevId, Integer nextId) {
        this.task = task;
        this.prevId = prevId;
        this.nextId = nextId;
    }

    public Task getTask() {
        return switch (task.getType()) {
            case EPIC -> (Epic) task;
            case SUBTASK -> (SubTask) task;
            default -> task;
        };
    }

    public Integer getId() {
        return task.getTaskId();
    }

    public Integer getPrevId() {
        return prevId;
    }

    public Integer getNextId() {
        return nextId;
    }

    public void setPrevId(Integer prevId) {
        this.prevId = prevId;
    }

    public void setNextId(Integer nextId) {
        this.nextId = nextId;
    }
}
