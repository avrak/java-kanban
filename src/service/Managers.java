package service;

import interfaces.FileTaskManager;
import interfaces.HistoryManager;

public class Managers {

    public Managers() {
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static FileTaskManager getDefaulf() {
        return new FileBackedTaskManager();
    }
}
