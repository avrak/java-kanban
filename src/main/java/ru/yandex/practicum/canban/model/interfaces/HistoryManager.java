package interfaces;

import main.java.ru.yandex.practicum.canban.model.Task;

import java.util.LinkedList;

public interface HistoryManager {
    void add(Task task);
    LinkedList<Task> getHistory ();
}
