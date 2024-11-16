import main.java.ru.yandex.practicum.canban.model.*;
import main.java.ru.yandex.practicum.canban.service.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


public class InMemoryTaskManagerTest {

    private InMemoryTaskManager taskManager;
    private Epic epic;
    private SubTask subTask;
    private Task task;

    @BeforeEach
    public void beforeEach() {
        taskManager = (InMemoryTaskManager) Managers.getDefaulf();
        epic = new Epic(TaskType.EPIC, "new Epic test", "Test addNewTask description");
        taskManager.addNewEpic(epic);
        subTask = new SubTask(TaskType.SUBTASK, epic.getTaskId(),"new addNewTask test", "Test addNewTask description");
        taskManager.addNewSubtask(subTask);
        task = new Task(TaskType.TASK, "new Task test", "Test addNewTask description");
        taskManager.addNewTask(task);

    }

    @Test
    public void addNewTask() {

        final int taskId = task.getTaskId();

        final Task savedTask = taskManager.getTask(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = taskManager.getTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    public void addNewEpic() {

        final int epicId = epic.getTaskId();

        final Epic savedEpic = taskManager.getEpic(epicId);

        assertNotNull(savedEpic, "Задача не найдена.");
        assertEquals(epic, savedEpic, "Задачи не совпадают.");

        final List<Epic> epics = taskManager.getEpics();

        assertNotNull(epics, "Задачи не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество задач.");
        assertEquals(epic, epics.get(0), "Задачи не совпадают.");
    }

    @Test
    public void addNewSubtask() {

        final int subTaskId = subTask.getTaskId();

        final SubTask savedSubTask = taskManager.getSubTask(subTaskId);

        assertNotNull(savedSubTask, "Задача не найдена.");
        assertEquals(subTask, savedSubTask, "Задачи не совпадают.");

        final List<SubTask> subTasks = taskManager.getSubTasks();

        assertNotNull(subTasks, "Задачи не возвращаются.");
        assertEquals(1, subTasks.size(), "Неверное количество задач.");
        assertEquals(subTask, subTasks.get(0), "Задачи не совпадают.");
    }

    @Test
    public void addSubTaskAsEpic() {

        final int subTaskId = subTask.getTaskId();

        final Epic savedWrongEpic = taskManager.getEpic(subTaskId);

        assertNull(savedWrongEpic, "Найдена подзадача в списке эпиков.");
    }

    @Test
    public void checkManagers() {

        assertNotNull(Managers.getDefaulf(), "Менеджер задач не создан.");
        assertNotNull(Managers.getDefaultHistory(), "Менеджер истории не создан.");
    }

    @Test
    public void checkHistory() {

        Epic epicInHistory = taskManager.getEpic(epic.getTaskId());
        SubTask subTaskInHistory = taskManager.getSubTask(subTask.getTaskId());
        Task taskInHistory = taskManager.getTask(task.getTaskId());

        ArrayList<Task> tasksInHistory = (ArrayList<Task>) taskManager.getHistory();

        assertTrue(tasksInHistory.contains(epicInHistory), "Эпик не найден в истории.");
        assertTrue(tasksInHistory.contains(subTaskInHistory), "Подзадача не найдена в истории.");
        assertTrue(tasksInHistory.contains(taskInHistory), "Задача не найдена в истории.");

    }
}