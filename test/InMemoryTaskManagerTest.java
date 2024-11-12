import main.java.ru.yandex.practicum.canban.model.*;
import main.java.ru.yandex.practicum.canban.service.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.testng.annotations.Test;

import static org.junit.jupiter.api.Assertions.*;


public class InMemoryTaskManagerTest {

    private static InMemoryTaskManager taskManager;
    private static Epic epic;
    private static SubTask subTask;
    private static Task task;

    protected void beforeEach() {
        taskManager = new InMemoryTaskManager();
        epic = new Epic(TaskType.EPIC, "new Epic test", "Test addNewTask description");
        taskManager.addNewEpic(epic);
        subTask = new SubTask(TaskType.SUBTASK, epic.getTaskId(),"new Epic test", "Test addNewTask description");
        taskManager.addNewSubtask(subTask);
        task = new Task(TaskType.TASK, "new Task test", "Test addNewTask description");
        taskManager.addNewTask(task);

    }

    @Test
    public void addNewTask() {
        beforeEach();

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

        beforeEach();

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

        beforeEach();

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

        beforeEach();

        final int subTaskId = subTask.getTaskId();

        final Epic savedWrongEpic = taskManager.getEpic(subTaskId);

        assertNull(savedWrongEpic, "Найдена подзадача в списке эпиков.");
    }

    @Test
    public void checkManagers() {
        Managers managers = new Managers();

        assertNotNull(managers.getDefaulf(), "Менеджер задач не создан.");
        assertNotNull(managers.getDefaultHistory(), "Менеджер истории не создан.");
    }

    @Test
    public void checkHistory() {

        beforeEach();

        Epic epicInHistory = taskManager.getEpic(epic.getTaskId());
        SubTask subTaskInHistory = taskManager.getSubTask(subTask.getTaskId());
        Task taskInHistory = taskManager.getTask(task.getTaskId());

        assertNotNull(taskManager.getHistory().contains(epic), "Эпик не найден в истории.");
        assertNotNull(taskManager.getHistory().contains(subTask), "Подзадача не найдена в истории.");
        assertNotNull(taskManager.getHistory().contains(task), "Задача не найден в истории.");
    }
}