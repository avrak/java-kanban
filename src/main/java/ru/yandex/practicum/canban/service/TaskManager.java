package main.java.ru.yandex.practicum.canban.service;

import main.java.ru.yandex.practicum.canban.model.*;

import java.util.*;

public class TaskManager {
    private HashMap<Integer, Task> tasksList;
    private HashMap<Integer, Epic> epicsList;
    private HashMap<Integer, SubTask> subTasksList;

    public TaskManager() {
        tasksList = new HashMap<>();
        epicsList = new HashMap<>();
        subTasksList = new HashMap<>();
    }

    private void updateEpicStatus(Epic epic) {
        // Нет подзадач - NEW
        if (epic.getSubTasksIds().isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
        }
        int tasksInNew = 0;
        int tasksInInProgress = 0;
        int tasksInDone = 0;

        for (Integer subTaskId : epic.getSubTasksIds()) {
            switch (subTasksList.get(subTaskId).getStatus()) {
                case NEW:
                    tasksInNew++;
                    break;
                case IN_PROGRESS:
                    tasksInInProgress++;
                    break;
                case DONE:
                    tasksInDone++;
                    break;
            }
        }
        if (tasksInNew == 0 && tasksInInProgress == 0 && tasksInDone > 0) {
            epic.setStatus(TaskStatus.DONE);
        } else if ((tasksInInProgress > 0 || (tasksInNew > 0 && tasksInDone > 0))){
            epic.setStatus(TaskStatus.IN_PROGRESS);
        } else {
            epic.setStatus(TaskStatus.NEW);
        }
    }

    public List<Task> getTasks() {
        return new ArrayList<Task>(tasksList.values());
    }

    public List<SubTask> getSubTasks() {
        return new ArrayList<SubTask>(subTasksList.values());
    }

    public List<Epic> getEpics() {
        return new ArrayList<Epic>(epicsList.values());
    }

    public List<SubTask> getEpicSubTasks(int epicId) {
        List<SubTask> epicSubTaskList  = new ArrayList<>();
        Epic epic = epicsList.get(epicId);
        for (int i : epic.getSubTasksIds()) {
            epicSubTaskList.add(subTasksList.get(i));
        }
        return epicSubTaskList;
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
        tasksList.put(task.getTaskId(), task);
    }

    public void updateEpic(Epic epic) {
        epicsList.put(epic.getTaskId(), epic);
    }

    public void updateSubtask(SubTask subTask) {
        subTasksList.put(subTask.getTaskId(), subTask);
        updateEpicStatus(getEpic(subTask.getEpicId()));
    }

    public void deleteTask(int id) {
        tasksList.remove(id);
    }

    public void deleteEpic(int id) {
        ArrayList<Integer> subTasksIds = epicsList.remove(id).getSubTasksIds();
        for (Integer subTaskId : subTasksIds) {
            subTasksList.remove(subTaskId);
        }
    }

    public void deleteSubTask(Integer id) {
        Epic epic = epicsList.get(subTasksList.get(id).getEpicId());
        subTasksList.remove(id);
        epic.deleteSubTask(id);
        updateEpicStatus(epic);
    }

    public void deleteTasks() {
        tasksList.clear();
    }

    public void deleteSubTasks() {
        subTasksList.clear();
        for (Epic epic : epicsList.values()) {
            epic.deleteAllSubTasksIds();
            updateEpicStatus(epic);
        }
    }

    public void deleteEpics() {
        subTasksList.clear();
        epicsList.clear();
    }
}
