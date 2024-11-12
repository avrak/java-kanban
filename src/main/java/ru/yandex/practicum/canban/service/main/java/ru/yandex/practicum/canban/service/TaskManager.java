package main.java.ru.yandex.practicum.canban.service;

import main.java.ru.yandex.practicum.canban.model.*;

import java.util.*;

interface TaskManager {

    public List<Task> getTasks();

    public List<SubTask> getSubTasks();

    public List<Epic> getEpics();

    public List<SubTask> getEpicSubTasks(int epicId);

    public Task getTask(int id);

    public SubTask getSubTask(int id);

    public Epic getEpic(int id);

    public void addNewTask(Task task);

    public void addNewEpic(Epic epic);

    public void addNewSubtask(SubTask subTask);

    public void updateTask(Task task);

    public void updateEpic(Epic epic);

    public void updateSubtask(SubTask subTask);

    public void deleteTask(int id);

    public void deleteEpic(int id);

    public void deleteSubTask(Integer id);

    public void deleteTasks();

    public void deleteSubTasks();

    public void deleteEpics();

    public ArrayList<Task> getHistory();
}
