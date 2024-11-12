package main.java.ru.yandex.practicum.canban.model;

import java.util.ArrayList;

public interface HistoryManager {
    public void add(Task task);
    public ArrayList<Task> getHistory ();
}
