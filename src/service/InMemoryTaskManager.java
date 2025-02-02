package service;

import interfaces.HistoryManager;
import interfaces.TaskManager;
import model.*;

import java.util.*;
import java.util.stream.Collectors;


public class InMemoryTaskManager implements TaskManager {
    private final HashMap<Integer, Task> tasksList;
    private final HashMap<Integer, Epic> epicsList;
    private final HashMap<Integer, SubTask> subTasksList;
    private final TreeSet<Task> prioritizedTasksList;
    private final HistoryManager inMemoryHistoryManager;

    public InMemoryTaskManager() {
        tasksList = new HashMap<>();
        epicsList = new HashMap<>();
        subTasksList = new HashMap<>();
        prioritizedTasksList = new TreeSet(new Comparator<Task>() {
            @Override
            public int compare(Task o1, Task o2) {
                if (o1 == null || o2 == null) {
                    return 1;
                } else if (((Task)o1).getStartTime().isBefore(((Task)o2).getStartTime())) {
                    return -1;
                } else if (((Task)o1).getStartTime().isAfter(((Task)o2).getStartTime())) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });
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
    public HashMap<Integer, Task> getTasks() {
        return new HashMap<>(tasksList);
    }

    @Override
    public HashMap<Integer, SubTask> getSubTasks() {
            return new HashMap<>(subTasksList);
    }

    @Override
    public HashMap<Integer, Epic> getEpics() {
        return new HashMap<>(epicsList);
    }

    @Override
    public List<SubTask> getEpicSubTasks(int epicId) {
        Epic epic = epicsList.get(epicId);
        return epic.getSubTasksIds()
                .stream()
                .map(subTasksList::get)
                .toList();
    }

    @Override
    public Task getTask(int id) {
        Task task = tasksList.get(id);
        if (task != null) {
            inMemoryHistoryManager.add(task);
        }
        return task;
    }

    @Override
    public SubTask getSubTask(int id) {
        SubTask subTask = subTasksList.get(id);
        if (subTask != null) {
            inMemoryHistoryManager.add(subTask);
        }
        return subTask;
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = epicsList.get(id);
        if (epic != null) {
            inMemoryHistoryManager.add(epic);
        }
        return epic;
    }

    @Override
    public void addNewTask(Task task) {
        if (!intersectedTasksIds(task).isEmpty()) {
            throw new UserInputException("Новая задача пересекается по времени с задачами ID: " +
                    intersectedTasksIds(task));
        }

        tasksList.put(task.getTaskId(), task);
        prioritizedTasksList.add(task);
    }

    @Override
    public void addNewEpic(Epic epic) {
        epicsList.put(epic.getTaskId(), epic);
    }

    @Override
    public void addNewSubtask(SubTask subTask) {
        if (intersectedTasksIds(subTask).isEmpty()) {
            subTasksList.put(subTask.getTaskId(), subTask);
            Epic epic = epicsList.get(subTask.getEpicId());
            epic.addSubTaskId(subTask.getTaskId());
            updateEpicStatus(epic);
            prioritizedTasksList.add(subTask);
        } else {
            throw new UserInputException("Новая задача пересекается по времени с задачами ID: " + intersectedTasksIds(subTask));
        }
    }

    @Override
    public void updateTask(Task task) {
        tasksList.put(task.getTaskId(), task);
        prioritizedTasksList.add(task);
    }

    @Override
    public void updateEpic(Epic epic) {
        epicsList.put(epic.getTaskId(), epic);
    }

    @Override
    public void updateSubtask(SubTask subTask) {
        subTasksList.put(subTask.getTaskId(), subTask);
        prioritizedTasksList.add(subTask);
        updateEpicStatus(getEpics().get(subTask.getEpicId()));
    }

    @Override
    public void deleteTask(int id) {
        prioritizedTasksList.remove(tasksList.get(id));
        tasksList.remove(id);
        inMemoryHistoryManager.remove(id);
    }

    @Override
    public void deleteEpic(int id) {
        ArrayList<Integer> subTasksIds = epicsList.remove(id).getSubTasksIds();
        inMemoryHistoryManager.remove(id);

        subTasksIds
                .forEach(subTaskId -> {
                    prioritizedTasksList.remove(subTasksList.get(subTaskId));
                    subTasksList.remove(subTaskId);
                    inMemoryHistoryManager.remove(subTaskId);
                });
    }

    @Override
    public void deleteSubTask(Integer id) {
        Epic epic = epicsList.get(subTasksList.get(id).getEpicId());
        prioritizedTasksList.remove(subTasksList.get(id));
        subTasksList.remove(id);
        epic.deleteSubTask(id);
        updateEpicStatus(epic);
        inMemoryHistoryManager.remove(id);
    }

    @Override
    public void deleteTasks() {
        tasksList.keySet()
                .forEach(id -> {
                    inMemoryHistoryManager.remove(id);
                    prioritizedTasksList.remove(tasksList.get(id));
                });
        tasksList.clear();
    }

    @Override
    public void deleteSubTasks() {
        subTasksList.keySet()
                .forEach(id -> {
                    inMemoryHistoryManager.remove(id);
                    prioritizedTasksList.remove(tasksList.get(id));
                });
        subTasksList.clear();

        epicsList.values()
                .forEach(epic -> {
                        epic.deleteAllSubTasksIds();
                        updateEpicStatus(epic);
                });
    }

    @Override
    public void deleteEpics() {
        subTasksList.keySet()
                .forEach(id -> {
                    inMemoryHistoryManager.remove(id);
                    prioritizedTasksList.remove(tasksList.get(id));
                });
        subTasksList.clear();

        epicsList.keySet()
                .forEach(inMemoryHistoryManager::remove);
        epicsList.clear();
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(inMemoryHistoryManager.getHistory());
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasksList);
    }

    private List<Integer> intersectedTasksIds(Task newTask) {
        return prioritizedTasksList
                .stream()
                .filter(task -> (// начало нового отрезка в пределах старого
                                task.getStartTime().isBefore(newTask.getStartTime())
                                        && task.getEndTime().isAfter(newTask.getStartTime())
                        ) || (// конец нового отрезка в пределах старого
                                task.getStartTime().isBefore(newTask.getEndTime())
                                        && task.getEndTime().isAfter(newTask.getEndTime())
                        ) || (// новый отрезок совпадает со старым
                                task.getStartTime().isEqual(newTask.getStartTime())
                                        && task.getEndTime().isEqual(newTask.getEndTime())
                        ) || (// старый отрезок внутри нового
                                task.getStartTime().isAfter(newTask.getStartTime())
                                        && task.getEndTime().isBefore(newTask.getEndTime())
                        )
                )
                .map(Task::getTaskId)
                .collect(Collectors.toList());
    }

    public void removePrioritizedTask(Task task) {
        prioritizedTasksList.remove(task);
    }
}
