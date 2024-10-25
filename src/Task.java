import java.util.HashMap;

public class Task extends TaskId {
    private final String SHIFT = "    ";

    private int taskId;
    private String shift;

    public TaskType getTaskType() {
        return taskType;
    }

    private TaskType taskType;
    private String name;
    private HashMap<Integer, Task> subTasks;

    private String description;

    private TaskStatus status;
    public Task (TaskType taskType, String name, String description) {
        this.taskId = getNewTaskId();
        if (taskType == TaskType.SUBTASK) {
            shift = SHIFT;
        } else {
            shift = "";
        }
        this.taskType = taskType;
        this.name = name;
        this.description = description;
        this.subTasks = new HashMap<>();
        this.status = TaskStatus.NEW;
    }

    @Override
    public String toString() {
        TaskStatus taskStatus = status;
        if (subTasks.size() != 0) {
            taskStatus = TaskStatus.DONE;
            for (int subTaskId : subTasks.keySet()) {
                if (!subTasks.get((subTaskId)).status.equals(TaskStatus.DONE)) {
                    taskStatus = TaskStatus.IN_PROGRESS;
                    break;
                }
            }
        }
        return shift + "Task{" +
                "taskId=" + taskId +
                ", taskType=" + taskType +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + taskStatus +
                '}';
    }

    public int getTaskId() {
        return taskId;
    }

    public HashMap<Integer, Task> getSubTasks() {
        return subTasks;
    }

    public void setName(String name) {
        this.name = name;
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

    public void addSubtask(Task task) {
        //subTasks.computeIfAbsent(taskId, k -> task);
        subTasks.put(task.getTaskId(), task);
    }
}
