package service;

import interfaces.HistoryManager;
import interfaces.TaskManager;

public class Managers {

    public Managers() {
    }

    public static TaskManager getDefaulf() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
