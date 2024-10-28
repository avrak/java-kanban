package main.java.ru.yandex.practicum.canban.service;

import main.java.ru.yandex.practicum.canban.model.*;

import java.util.*;

public class TaskManager {
    HashMap<Integer, Task> tasksList;
    HashMap<Integer, Epic> epicsList;
    HashMap<Integer, SubTask> subTasksList;
    Scanner scanner;

    public TaskManager(Scanner scanner) {
        tasksList = new HashMap<>();
        epicsList = new HashMap<>();
        subTasksList = new HashMap<>();
        this.scanner = scanner;
    }

    public void updateEpicStatus(Epic epic) {
        epic.setStatus(TaskStatus.DONE);
        for (Integer subTaskId : epic.getSubTasksIds()) {
            if (subTasksList.get(subTaskId).getStatus() != TaskStatus.DONE) {
                epic.setStatus(TaskStatus.IN_PROGRESS);
                break;
            }
        }
    }

    public HashMap<Integer, Task> getTasks() {
        return tasksList;
    }

    public HashMap<Integer, SubTask> getSubTasks() {
        return subTasksList;
    }

    public HashMap<Integer, Epic> getEpics() {
        return epicsList;
    }

    public Task getTask(int id) {
        return tasksList.get(id);
    }

    public SubTask getSubTask(int id) {
        return subTasksList.get(id);
    }

    public Epic getEpic(int id) {
        return epicsList.get(id);
    }

    public void addNewTask(Task task) {
        tasksList.put(task.getTaskId(), task);
    }

    public void addNewEpic(Epic epic) {
        epicsList.put(epic.getTaskId(), epic);
    }

    public void addNewSubtask(SubTask subTask) {
        subTasksList.put(subTask.getTaskId(), subTask);
        Epic epic = epicsList.get(subTask.getEpicId());
        epic.addSubTaskId(subTask.getTaskId());
        updateEpicStatus(epic);
    }

    public void updateTask(Task task) {
        deleteTask(task.getTaskId());
        addNewTask(task);
    }

    public void updateEpic(Epic epic) {
        ArrayList<Integer> subTasksIds = new ArrayList<>();
        subTasksIds = (ArrayList<Integer>) epic.getSubTasksIds().clone();
        epicsList.remove(epic.getTaskId());
        addNewEpic(epic);
        for (Integer subTaskId : subTasksIds) {
            epic.addSubTaskId(subTaskId);
        }
    }

    public void updateSubtask(SubTask subtask) {
        deleteSubTask(subtask.getTaskId());
        addNewSubtask(subtask);
    }

    public void deleteTask(int id) {
        tasksList.remove(id);
    }

    public void deleteEpic(int id) {
        ArrayList<Integer> subTasksIds = epicsList.get(id).getSubTasksIds();
        for (Integer subTaskId : subTasksIds) {
            deleteSubTask(subTaskId);
        }
        epicsList.remove(id);
    }

    public void deleteSubTask(int id) {
        subTasksList.remove(id);
    }

    public void deleteTasks() {
        tasksList.clear();
    }

    public void deleteSubTasks() {
        subTasksList.clear();
    }

    public void deleteEpics() {
        epicsList.clear();
    }
}
