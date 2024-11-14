package main.java.ru.yandex.practicum.canban.service;

import interfaces.HistoryManager;
import interfaces.TaskManager;

public class Managers {

   public TaskManager getDefaulf() {
        return new InMemoryTaskManager();
    };
    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
