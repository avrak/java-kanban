package interfaces;

import main.java.ru.yandex.practicum.canban.model.*;

import java.util.*;

public interface TaskManager {

    List<Task> getTasks();

    List<SubTask> getSubTasks();

    List<Epic> getEpics();

    List<SubTask> getEpicSubTasks(int epicId);

    Task getTask(int id);

    SubTask getSubTask(int id);

    Epic getEpic(int id);

    void addNewTask(Task task);

    void addNewEpic(Epic epic);

    void addNewSubtask(SubTask subTask);

    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubtask(SubTask subTask);

    void deleteTask(int id);

    void deleteEpic(int id);

    void deleteSubTask(Integer id);

    void deleteTasks();

    void deleteSubTasks();

    void deleteEpics();

    List<Task> getHistory();
}
