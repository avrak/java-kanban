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

        final Task savedTask = taskManager.getTasks().get(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = new ArrayList<>(taskManager.getTasks().values());

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

        final List<Epic> epics = new ArrayList<>(taskManager.getEpics().values());

        assertNotNull(epics, "Задачи не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество задач.");
        assertEquals(epic, epics.get(0), "Задачи не совпадают.");
    }

    @Test
    public void addNewSubtask() {

        final int subTaskId = subTask.getTaskId();

        final SubTask savedSubTask = taskManager.getSubTasks().get(subTaskId);

        assertNotNull(savedSubTask, "Задача не найдена.");
        assertEquals(subTask, savedSubTask, "Задачи не совпадают.");

        final List<SubTask> subTasks = new ArrayList<>(taskManager.getSubTasks().values());

        assertNotNull(subTasks, "Задачи не возвращаются.");
        assertEquals(1, subTasks.size(), "Неверное количество задач.");
        assertEquals(subTask, subTasks.get(0), "Задачи не совпадают.");
    }

    @Test
    public void addSubTaskAsEpic() {

        final int subTaskId = subTask.getTaskId();

        final Epic savedWrongEpic = taskManager.getEpics().get(subTaskId);

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
        assertNull(taskManager.getSubTasks().get(task.getTaskId()), "Задача не был удалена");
    }

    @Test
    public void checkRemoveEpic() {

        taskManager.deleteEpic(epic.getTaskId());
        assertNull(taskManager.getEpics().get(epic.getTaskId()), "Эпик не был удалён");
        assertNull(taskManager.getSubTasks().get(subTask.getTaskId()), "Подзадача не была удалена вместе с эпиком");
    }

    @Test
    public void checkRemoveSubtask() {

        taskManager.deleteSubTask(subTask.getTaskId());
        assertNull(taskManager.getSubTasks().get(subTask.getTaskId()), "Подзадача не был удалена");
    }

    @Test
    public void checkHistoryExists() {

        Epic epicInHistory = taskManager.getEpic(epic.getTaskId());
        SubTask subTaskInHistory = taskManager.getSubTask(subTask.getTaskId());
        Task taskInHistory = taskManager.getTask(task.getTaskId());

        ArrayList<Task> tasksInHistory = (ArrayList<Task>) taskManager.getHistory();

        assertTrue(tasksInHistory.contains(epicInHistory), "Эпик не найден в истории.");
        assertTrue(tasksInHistory.contains(subTaskInHistory), "Подзадача не найдена в истории.");
        assertTrue(tasksInHistory.contains(taskInHistory), "Задача не найдена в истории.");

    }

    @Test
    public void checkHistoryCount() {

        Epic epicInHistory = taskManager.getEpic(epic.getTaskId());
        SubTask subTaskInHistory = taskManager.getSubTask(subTask.getTaskId());
        Task taskInHistory = taskManager.getTask(task.getTaskId());

        taskInHistory = taskManager.getTask(task.getTaskId());
        subTaskInHistory = taskManager.getSubTask(subTask.getTaskId());
        epicInHistory = taskManager.getEpic(epic.getTaskId());

        ArrayList<Task> tasksInHistory = (ArrayList<Task>) taskManager.getHistory();

        assertEquals(3, tasksInHistory.size(), "Найдено больше одного упоминания в истории.");
    }

    @Test
    public void checkHistoryOrder() {

        Epic epicInHistory = taskManager.getEpic(epic.getTaskId());
        SubTask subTaskInHistory = taskManager.getSubTask(subTask.getTaskId());
        Task taskInHistory = taskManager.getTask(task.getTaskId());

        taskInHistory = taskManager.getTask(task.getTaskId());
        subTaskInHistory = taskManager.getSubTask(subTask.getTaskId());
        epicInHistory = taskManager.getEpic(epic.getTaskId());

        ArrayList<Task> tasksInHistory = (ArrayList<Task>) taskManager.getHistory();

        assertEquals(epicInHistory.getTaskId(), tasksInHistory.get(0).getTaskId(), "Неверный порядок в истории просмотров");
        assertEquals(subTaskInHistory.getTaskId(), tasksInHistory.get(1).getTaskId(), "Неверный порядок в истории просмотров");
        assertEquals(taskInHistory.getTaskId(), tasksInHistory.get(2).getTaskId(), "Неверный порядок в истории просмотров");
    }

    @Test
    public void checkHistoryDelete() {

        Epic epicInHistory = taskManager.getEpic(epic.getTaskId());
        SubTask subTaskInHistory = taskManager.getSubTask(subTask.getTaskId());
        Task taskInHistory = taskManager.getTask(task.getTaskId());

        taskManager.deleteEpic(epic.getTaskId());

        ArrayList<Task> tasksInHistory = (ArrayList<Task>) taskManager.getHistory();

        assertFalse(tasksInHistory.contains(epicInHistory), "Эпик не удалён из истории.");
        assertFalse(tasksInHistory.contains(subTaskInHistory), "Подзадача не удалена из истории.");

    }

    @Test
    public void checkHistoryDeleteAllSubtasks() {
        SubTask subTaskInHistory = taskManager.getSubTask(subTask.getTaskId());

        taskManager.deleteSubTasks();

        ArrayList<Task> tasksInHistory = (ArrayList<Task>) taskManager.getHistory();

        assertFalse(tasksInHistory.contains(subTaskInHistory), "Подзадача не удалена из истории при удалении всех задач.");
    }

    @Test
    public void checkHistoryDeleteAllTasksAndEpics() {

        Epic epicInHistory = taskManager.getEpic(epic.getTaskId());
        SubTask subTaskInHistory = taskManager.getSubTask(subTask.getTaskId());
        Task taskInHistory = taskManager.getTask(task.getTaskId());

        taskManager.deleteEpics();
        taskManager.deleteTasks();

        ArrayList<Task> tasksInHistory = (ArrayList<Task>) taskManager.getHistory();

        assertFalse(tasksInHistory.contains(epicInHistory), "Эпик не удалён из истории при удалении всех задач.");
        assertFalse(tasksInHistory.contains(subTaskInHistory), "Подзадача не удалена из истории при удалении всех задач.");
        assertFalse(tasksInHistory.contains(taskInHistory), "Подзадача не удалена из истории при удалении всех задач.");
    }
}