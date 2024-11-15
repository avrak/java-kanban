package interfaces;

import main.java.ru.yandex.practicum.canban.model.Task;

import java.util.List;

public interface HistoryManager {
    void add(Task task);
    List<Task> getHistory();
}
