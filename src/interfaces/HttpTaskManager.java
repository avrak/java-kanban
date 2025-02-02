package interfaces;

import service.FileBackedTaskManager;

public interface HttpTaskManager {
    static FileBackedTaskManager getFileBackedTaskManager(String fileName) {
        return null;
    }

    FileBackedTaskManager getHttpBackedTaskManager();
}
