package main.java.ru.yandex.practicum.canban.service;

import main.java.ru.yandex.practicum.canban.model.Epic;
import main.java.ru.yandex.practicum.canban.model.SubTask;
import main.java.ru.yandex.practicum.canban.model.Task;
import main.java.ru.yandex.practicum.canban.model.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager{
    private HashMap<Integer, Task> tasksList;
    private HashMap<Integer, Epic> epicsList;
    private HashMap<Integer, SubTask> subTasksList;
    private InMemoryHistoryManager inMemoryHistoryManager;

    public InMemoryTaskManager() {
        tasksList = new HashMap<>();
        epicsList = new HashMap<>();
        subTasksList = new HashMap<>();
        inMemoryHistoryManager = new InMemoryHistoryManager();
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

    @Override
    public List<Task> getTasks() {
        return new ArrayList<Task>(tasksList.values());
    }

    @Override
    public List<SubTask> getSubTasks() {
        return new ArrayList<SubTask>(subTasksList.values());
    }

    @Override
    public List<Epic> getEpics() {
        return new ArrayList<Epic>(epicsList.values());
    }

    @Override
    public List<SubTask> getEpicSubTasks(int epicId) {
        List<SubTask> epicSubTaskList  = new ArrayList<>();
        Epic epic = epicsList.get(epicId);
        for (int i : epic.getSubTasksIds()) {
            epicSubTaskList.add(subTasksList.get(i));
        }
        return epicSubTaskList;
    }

    @Override
    public Task getTask(int id) {
        Task task = tasksList.get(id);
        inMemoryHistoryManager.add(task);
        return task;
    }

    @Override
    public SubTask getSubTask(int id) {
        SubTask subTask = subTasksList.get(id);
        return subTask;
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = epicsList.get(id);
        return epic;
    }

    @Override
    public void addNewTask(Task task) {
        tasksList.put(task.getTaskId(), task);
    }

    @Override
    public void addNewEpic(Epic epic) {
        epicsList.put(epic.getTaskId(), epic);
    }

    @Override
    public void addNewSubtask(SubTask subTask) {
        subTasksList.put(subTask.getTaskId(), subTask);
        Epic epic = epicsList.get(subTask.getEpicId());
        epic.addSubTaskId(subTask.getTaskId());
        updateEpicStatus(epic);
    }

    @Override
    public void updateTask(Task task) {
        tasksList.put(task.getTaskId(), task);
    }

    @Override
    public void updateEpic(Epic epic) {
        epicsList.put(epic.getTaskId(), epic);
    }

    @Override
    public void updateSubtask(SubTask subTask) {
        subTasksList.put(subTask.getTaskId(), subTask);
        updateEpicStatus(getEpic(subTask.getEpicId()));
    }

    @Override
    public void deleteTask(int id) {
        tasksList.remove(id);
    }

    @Override
    public void deleteEpic(int id) {
        ArrayList<Integer> subTasksIds = epicsList.remove(id).getSubTasksIds();
        for (Integer subTaskId : subTasksIds) {
            subTasksList.remove(subTaskId);
        }
    }

    @Override
    public void deleteSubTask(Integer id) {
        Epic epic = epicsList.get(subTasksList.get(id).getEpicId());
        subTasksList.remove(id);
        epic.deleteSubTask(id);
        updateEpicStatus(epic);
    }

    @Override
    public void deleteTasks() {
        tasksList.clear();
    }

    @Override
    public void deleteSubTasks() {
        subTasksList.clear();
        for (Epic epic : epicsList.values()) {
            epic.deleteAllSubTasksIds();
            updateEpicStatus(epic);
        }
    }

    @Override
    public void deleteEpics() {
        subTasksList.clear();
        epicsList.clear();
    }

    @Override
    public ArrayList<Task> getHistory() {
        return inMemoryHistoryManager.getHistory();
    }
}
