package main.java.ru.yandex.practicum.canban.service;

import interfaces.HistoryManager;
import main.java.ru.yandex.practicum.canban.model.Task;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final static int LAST_VIEWED = 0;
    private final static int TO_REMOVE = 10;

    private final List<Task> lastViewedTenTasks;

    public InMemoryHistoryManager() {
        lastViewedTenTasks = new LinkedList<>();
    }

    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }
        lastViewedTenTasks.add(LAST_VIEWED, task);
        if(lastViewedTenTasks.size() > TO_REMOVE) {
            lastViewedTenTasks.remove(TO_REMOVE);
        }
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<Task>(lastViewedTenTasks);
    }
}
