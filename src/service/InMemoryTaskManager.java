package service;

import interfaces.HistoryManager;
import interfaces.TaskManager;
import model.Epic;
import model.SubTask;
import model.Task;
import model.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class InMemoryTaskManager implements TaskManager {
    private final HashMap<Integer, Task> tasksList;
    private final HashMap<Integer, Epic> epicsList;
    private final HashMap<Integer, SubTask> subTasksList;
    private final HistoryManager inMemoryHistoryManager;

    public InMemoryTaskManager() {
        tasksList = new HashMap<>();
        epicsList = new HashMap<>();
        subTasksList = new HashMap<>();
        inMemoryHistoryManager = Managers.getDefaultHistory();
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
        } else if ((tasksInInProgress > 0 || (tasksInNew > 0 && tasksInDone > 0))) {
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
        return tasksList.get(id);
    }

    @Override
    public SubTask getSubTask(int id) {
        return subTasksList.get(id);
    }

    @Override
    public Epic getEpic(int id) {
        return epicsList.get(id);
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
        inMemoryHistoryManager.remove(id);
    }

    @Override
    public void deleteEpic(int id) {
        ArrayList<Integer> subTasksIds = epicsList.remove(id).getSubTasksIds();
        inMemoryHistoryManager.remove(id);
        for (Integer subTaskId : subTasksIds) {
            subTasksList.remove(subTaskId);
            inMemoryHistoryManager.remove(subTaskId);
        }
    }

    @Override
    public void deleteSubTask(Integer id) {
        Epic epic = epicsList.get(subTasksList.get(id).getEpicId());
        subTasksList.remove(id);
        epic.deleteSubTask(id);
        updateEpicStatus(epic);
        inMemoryHistoryManager.remove(id);
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
    public List<Task> getHistory() {
        return new ArrayList<>(inMemoryHistoryManager.getHistory());
    }

    @Override
    public Task viewTask(int id) {
        Task task = tasksList.get(id);
        if (task != null) {
            inMemoryHistoryManager.remove(id);
            inMemoryHistoryManager.add(task);
        }
        return task;
    }

    @Override
    public SubTask viewSubTask(int id) {
        SubTask subTask = subTasksList.get(id);
        if (subTask != null) {
            inMemoryHistoryManager.remove(id);
            inMemoryHistoryManager.add(subTask);
        }
        return subTask;
    }

    @Override
    public Epic viewEpic(int id) {
        Epic epic = epicsList.get(id);
        if (epic != null) {
            inMemoryHistoryManager.remove(id);
            inMemoryHistoryManager.add(epic);
        }
        return epic;
    }

}