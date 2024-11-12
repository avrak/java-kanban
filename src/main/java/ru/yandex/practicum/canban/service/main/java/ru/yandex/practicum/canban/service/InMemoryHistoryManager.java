package main.java.ru.yandex.practicum.canban.service;

import main.java.ru.yandex.practicum.canban.model.HistoryManager;
import main.java.ru.yandex.practicum.canban.model.Task;

import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {
    private ArrayList<Task> lastViewedTenTasks;

    public InMemoryHistoryManager() {
        lastViewedTenTasks = new ArrayList<>();
    }

    @Override
    public void add(Task task) {
        lastViewedTenTasks.add(0, task);
        if(lastViewedTenTasks.size() > 10) {
            lastViewedTenTasks.remove(10);
        }
    }

    @Override
    public ArrayList<Task> getHistory() {
        return lastViewedTenTasks;
    }
}
