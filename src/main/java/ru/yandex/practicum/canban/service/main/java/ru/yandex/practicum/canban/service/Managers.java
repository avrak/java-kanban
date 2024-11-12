package main.java.ru.yandex.practicum.canban.service;

import main.java.ru.yandex.practicum.canban.model.Epic;
import main.java.ru.yandex.practicum.canban.model.HistoryManager;
import main.java.ru.yandex.practicum.canban.model.SubTask;
import main.java.ru.yandex.practicum.canban.model.Task;

import java.util.ArrayList;
import java.util.List;

public class Managers {
    private final TaskManager manager;
    private static InMemoryHistoryManager inMemoryHistoryManager;

    public Managers() {
        inMemoryHistoryManager = new InMemoryHistoryManager();

        manager = new TaskManager() {
            @Override
            public List<Task> getTasks() {
                return null;
            }

            @Override
            public List<SubTask> getSubTasks() {
                return null;
            }

            @Override
            public List<Epic> getEpics() {
                return null;
            }

            @Override
            public List<SubTask> getEpicSubTasks(int epicId) {
                return null;
            }

            @Override
            public Task getTask(int id) {
                return null;
            }

            @Override
            public SubTask getSubTask(int id) {
                return null;
            }

            @Override
            public Epic getEpic(int id) {
                return null;
            }

            @Override
            public void addNewTask(Task task) {

            }

            @Override
            public void addNewEpic(Epic epic) {

            }

            @Override
            public void addNewSubtask(SubTask subTask) {

            }

            @Override
            public void updateTask(Task task) {

            }

            @Override
            public void updateEpic(Epic epic) {

            }

            @Override
            public void updateSubtask(SubTask subTask) {

            }

            @Override
            public void deleteTask(int id) {

            }

            @Override
            public void deleteEpic(int id) {

            }

            @Override
            public void deleteSubTask(Integer id) {

            }

            @Override
            public void deleteTasks() {

            }

            @Override
            public void deleteSubTasks() {

            }

            @Override
            public void deleteEpics() {

            }

            @Override
            public ArrayList<Task> getHistory() {
                return null;
            }
        };
    }
    public TaskManager getDefaulf() {
        return manager;
    }
    public static InMemoryHistoryManager getDefaultHistory() {
        return inMemoryHistoryManager;
    }
}
