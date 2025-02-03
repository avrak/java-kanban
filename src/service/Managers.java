package service;

import interfaces.HistoryManager;
import interfaces.TaskManager;

import java.io.IOException;

public class Managers {

    public Managers() {
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }


    public static TaskManager getDefaulf(String fileName) {
        return new FileBackedTaskManager(fileName);
    }

    public static HttpTaskServer getDefaultHttp(String fileName) throws IOException {
        return new HttpTaskServer(fileName);
    }
}
