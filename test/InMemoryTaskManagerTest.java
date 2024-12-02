import model.*;
import service.*;

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
    public void checkRemoveTask() {

        taskManager.deleteTask(task.getTaskId());
        assertNull(taskManager.getSubTask(task.getTaskId()), "Задача не был удалена");
    }

    @Test
    public void checkRemoveEpic() {

        taskManager.deleteEpic(epic.getTaskId());
        assertNull(taskManager.getEpic(epic.getTaskId()), "Эпик не был удалён");
        assertNull(taskManager.getSubTask(subTask.getTaskId()), "Подзадача не была удалена вместе с эпиком");
    }

    @Test
    public void checkRemoveSubtask() {

        taskManager.deleteSubTask(subTask.getTaskId());
        assertNull(taskManager.getSubTask(subTask.getTaskId()), "Подзадача не был удалена");
    }

    @Test
    public void checkHistoryExists() {

        Epic epicInHistory = taskManager.viewEpic(epic.getTaskId());
        SubTask subTaskInHistory = taskManager.viewSubTask(subTask.getTaskId());
        Task taskInHistory = taskManager.viewTask(task.getTaskId());

        ArrayList<Task> tasksInHistory = (ArrayList<Task>) taskManager.getHistory();

        assertTrue(tasksInHistory.contains(epicInHistory), "Эпик не найден в истории.");
        assertTrue(tasksInHistory.contains(subTaskInHistory), "Подзадача не найдена в истории.");
        assertTrue(tasksInHistory.contains(taskInHistory), "Задача не найдена в истории.");

    }

    @Test
    public void checkHistoryCount() {

        Epic epicInHistory = taskManager.viewEpic(epic.getTaskId());
        SubTask subTaskInHistory = taskManager.viewSubTask(subTask.getTaskId());
        Task taskInHistory = taskManager.viewTask(task.getTaskId());

        taskInHistory = taskManager.viewTask(task.getTaskId());
        subTaskInHistory = taskManager.viewSubTask(subTask.getTaskId());
        epicInHistory = taskManager.viewEpic(epic.getTaskId());

        ArrayList<Task> tasksInHistory = (ArrayList<Task>) taskManager.getHistory();

        assertEquals(3, tasksInHistory.size(), "Найдено больше одного упоминания в истории.");
    }

    @Test
    public void checkHistoryOrder() {

        Epic epicInHistory = taskManager.viewEpic(epic.getTaskId());
        SubTask subTaskInHistory = taskManager.viewSubTask(subTask.getTaskId());
        Task taskInHistory = taskManager.viewTask(task.getTaskId());

        taskInHistory = taskManager.viewTask(task.getTaskId());
        subTaskInHistory = taskManager.viewSubTask(subTask.getTaskId());
        epicInHistory = taskManager.viewEpic(epic.getTaskId());

        ArrayList<Task> tasksInHistory = (ArrayList<Task>) taskManager.getHistory();

        assertEquals(epicInHistory.getTaskId(), tasksInHistory.get(0).getTaskId(), "Неверный порядок в истории просмотров");
        assertEquals(subTaskInHistory.getTaskId(), tasksInHistory.get(1).getTaskId(), "Неверный порядок в истории просмотров");
        assertEquals(taskInHistory.getTaskId(), tasksInHistory.get(2).getTaskId(), "Неверный порядок в истории просмотров");
    }

    @Test
    public void checkHistoryDelete() {

        Epic epicInHistory = taskManager.viewEpic(epic.getTaskId());
        SubTask subTaskInHistory = taskManager.viewSubTask(subTask.getTaskId());
        Task taskInHistory = taskManager.viewTask(task.getTaskId());

        taskManager.deleteEpic(epic.getTaskId());

        ArrayList<Task> tasksInHistory = (ArrayList<Task>) taskManager.getHistory();

        assertFalse(tasksInHistory.contains(epicInHistory), "Эпик не удалён из истории.");
        assertFalse(tasksInHistory.contains(subTaskInHistory), "Подзадача не удалена из истории.");

    }

}