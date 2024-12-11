package service;

import interfaces.HistoryManager;
import interfaces.TaskManager;

public class Managers {

    public Managers() {
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }


    public static TaskManager getDefaulf(String fileName) {
        return new FileBackedTaskManager(fileName);
    }
}
