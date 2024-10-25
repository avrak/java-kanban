import java.util.HashMap;
import java.util.Scanner;

public class TaskManager {
    HashMap<Integer, Task> tasksList;
    Scanner scanner;

    public TaskManager(Scanner scanner) {
        tasksList = new HashMap<>();
        this.scanner = scanner;
    }

    public TaskType getTaskType(String taskTypeString) {
        for (TaskType taskType : TaskType.values()) {
            if (taskType.toString().equals(taskTypeString)) {
                return taskType;
            }
        }
        System.out.println("Неизвестный тип задачи, попробуйте ещё раз");
        return null;
    }

    public void printAllTasksList() {
        for (int taskId : tasksList.keySet()) {
            Task task = tasksList.get(taskId);
            System.out.println(task);
            HashMap<Integer, Task> subTasks = task.getSubTasks();
            if (subTasks != null) {
                for (int taskKey : subTasks.keySet()){
                    Task subTask = subTasks.get(taskKey);
                    System.out.println(subTask);
                }
            }
        }
    }

    public void clearAllTasks() {
        tasksList.clear();
    }

    public void printTask(int taskId) {
        System.out.println(tasksList.get(taskId));
    }

    public String setTaskName() {
        System.out.print("Введите название задачи: ");
        return scanner.next();
    }

    public String setTaskDescription() {
        System.out.print("Введите описание задачи: ");
        return scanner.next();
    }

    public TaskStatus setTaskStatus() {
        System.out.print("Введите статус задачи: ");
        String statusString = scanner.next();
        for (TaskStatus status : TaskStatus.values()) {
            if (status.toString().equals(statusString)) {
                return status;
            }
        }
        System.out.println("Некорректный статус, попробуйте ещё раз");
        return null;
    }

    public void addTask() {
        TaskType taskType;
        int parenTaskId = 0;
        String name;
        String description;

        System.out.print("Введите тип задачи: ");
        String taskTypeString = scanner.next();
        taskType = getTaskType(taskTypeString);
        if (taskType == null) {
            return;
        }

        name = setTaskName();
        description = setTaskDescription();
        Task task = new Task(taskType, name, description);

        if (taskType == TaskType.SUBTASK) {
            System.out.print("Введите ID эпика: ");
            parenTaskId = scanner.nextInt();
            Task parentTask = tasksList.get(parenTaskId);
            if (parentTask == null) {
                System.out.println("Эпик не найден, попробуйте ещё раз");
                return;
            } else {
                parentTask.addSubtask(task);
            }
        } else {
            tasksList.put(task.getTaskId(), task);
        }
    }

    public Task getTask(int taskId) {
        Task task = tasksList.get(taskId);
        if (task == null) {
            for (int epicTaskId : tasksList.keySet()) {
                HashMap<Integer, Task> subTasks = tasksList.get(epicTaskId).getSubTasks();
                if (subTasks != null) {
                    task = subTasks.get(taskId);
                    if (task != null) {
                        break;
                    };
                }
            }
        }
        return task;
    }

    public void updateTask(int taskId) {
        Task task = getTask(taskId);
        if (task == null) {
            System.out.println("Задача с ID=" + taskId + " не найдена");
            return;
        } else {
            task.setName(setTaskName());
            task.setDescription(setTaskDescription());
            if (task.getTaskType() != TaskType.EPIC) {
                task.setStatus(setTaskStatus());
            }
        }
    }

    public void deleteTask(int taskId) {
        Task task = tasksList.get(taskId);
        boolean done = false;

        if (task != null) {
            tasksList.remove(taskId);
            done = true;
        } else {
            for (int epicTaskId : tasksList.keySet()) {
                HashMap<Integer, Task> subTasks = tasksList.get(epicTaskId).getSubTasks();
                if (subTasks != null) {
                    task = subTasks.get(taskId);
                    if (task != null) {
                        subTasks.remove(taskId);
                        done = true;
                    };
                }
            }
        }
        if (done) {
            System.out.println("Задача удалена");
        } else {
            System.out.println("Задача с ID=" + taskId + " не найдена");
        }
    }

}
