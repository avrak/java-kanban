package model;

public class Node {
    private final Task task;
    private Node prevNode;
    private Node nextNode;

    public Node(Task task, Node prevNode, Node nextNode) {
        this.task = task;
        this.prevNode = prevNode;
        this.nextNode = nextNode;
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

    public Node getPrevNode() {
        return prevNode;
    }

    public Node getNextNode() {
        return nextNode;
    }

    public void setPrevNode(Node prevNode) {
        this.prevNode = prevNode;
    }

    public void setNextNode(Node nextNode) {
        this.nextNode = nextNode;
    }
}
