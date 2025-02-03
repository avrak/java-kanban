import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.FileBackedTaskManager;
import service.HttpTaskServer;
import service.InMemoryHistoryManager;
import service.Managers;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryTaskManagerTest {
    public static final String EMPTY_FILE = "";

    private FileBackedTaskManager taskManager;
    private InMemoryHistoryManager historyManager;
    private Epic epic;
    private SubTask subTask;
    private Task task;
    Gson gson = new GsonBuilder()
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();
    static HttpTaskServer httpTaskManager;
    private Epic httpEpic;
    private Epic httpEpicWithSubTasks;
    private SubTask httpSubTask;
    private Task httpTask;

    @BeforeEach
    public void beforeEach() throws IOException {
        taskManager = (FileBackedTaskManager) Managers.getDefaulf(EMPTY_FILE);
        historyManager = (InMemoryHistoryManager) Managers.getDefaultHistory();
        epic = new Epic(TaskType.EPIC, "new Epic test", "Test addNewTask description");
        taskManager.addNewEpic(epic);
        subTask = new SubTask(TaskType.SUBTASK, epic,"new SubTask test", "Test addNewTask description", Duration.ofMinutes(10), LocalDateTime.parse("01-01-2025 13:00", (Task.DATE_TIME_FORMATTER)));
        taskManager.addNewSubtask(subTask);
        task = new Task(TaskType.TASK, "new Task test", "Test addNewTask description", Duration.ofMinutes(10), LocalDateTime.parse("01-01-2025 12:30", (Task.DATE_TIME_FORMATTER)));
        taskManager.addNewTask(task);
        httpEpic = new Epic(TaskType.EPIC, "new HTTP Epic test", "Test HTTP addNewEpic description");
        httpEpicWithSubTasks = new Epic(TaskType.EPIC, "new HTTP Epic test with Subtasks", "Test HTTP addNewEpic with Subtasks description");
        httpSubTask = new SubTask(TaskType.SUBTASK, httpEpic,"new HTTP SubTask test", "Test addNewTask description", Duration.ofMinutes(10), LocalDateTime.parse("01-01-2025 14:00", (Task.DATE_TIME_FORMATTER)));
        httpTask = new Task(TaskType.TASK, "new HTTP Task test", "Test HTTP addNewTask description", Duration.ofMinutes(10), LocalDateTime.parse("01-01-2025 11:30", (Task.DATE_TIME_FORMATTER)));
    }

    @BeforeAll
    public static void beforeAll() throws IOException {
        httpTaskManager = Managers.getDefaultHttp(EMPTY_FILE);
        try {
            httpTaskManager.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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

        assertNotNull(Managers.getDefaulf(EMPTY_FILE), "Менеджер задач не создан.");
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

    @Test
    public void checkHistoryManagerAdd() {

        historyManager.add(task);

        ArrayList<Task> history = (ArrayList<Task>) historyManager.getHistory();

        assertTrue(history.contains(task), "Задача не добавлена в историю");
    }

    @Test
    public void checkHistoryManagerRemove() {

        historyManager.add(task);
        historyManager.remove(task.getTaskId());

        ArrayList<Task> history = (ArrayList<Task>) historyManager.getHistory();

        assertFalse(history.contains(task), "Задача не удалена из истории");
    }

    @Test
    public void checkFileCreated() {
        assertTrue(taskManager.getTmpFile().isFile(), "Файл с данными не создан");
    }

    @Test
    public void checkFileWasWritten() {
        File file = taskManager.getTmpFile();

        try {
            String data = Files.readString(file.toPath());
            assertTrue(data.contains(task.toFileString()), "Задача с типом TASK не сохранена в файл с данными");
            assertTrue(data.contains(epic.toFileString()), "Задача с типом EPIC не сохранена в файл с данными");
            assertTrue(data.contains(subTask.toFileString()), "Задача с типом SUBTASK не сохранена в файл с данными");
        } catch (IOException e) {
            fail("Не удалось прочитать файл с данными");
        }
    }

    @Test
    public void checkDataWasRestoredFromFile() {
        String inMemoryData = taskManager.getTasks().toString() + taskManager.getEpics().toString() + taskManager.getSubTasks();
        File file = taskManager.getTmpFile();

        taskManager =  (FileBackedTaskManager) Managers.getDefaulf(EMPTY_FILE);
        FileBackedTaskManager.restore(taskManager, file.getAbsolutePath());

        String restoredData = taskManager.getTasks().toString() + taskManager.getEpics().toString() + taskManager.getSubTasks();

        assertEquals(inMemoryData, restoredData, "Данные из файла восстановлены некорректно");
    }

    @Test
    public void checkTasksPriority() {

        ArrayList<Task> prioritizedList = new ArrayList<>(taskManager.getPrioritizedTasks());
        assertTrue(prioritizedList.get(0).toString().contains("new Task test")
                &&prioritizedList.get(1).toString().contains("new SubTask test")
                ,"Некорректная приоритизация");

        Task newTask = new Task(TaskType.TASK, "new Task test 2", "Test addNewTask description"
                , Duration.ofMinutes(10), LocalDateTime.parse("01-01-2025 12:45", (Task.DATE_TIME_FORMATTER)));
        taskManager.addNewTask(newTask);

        prioritizedList = new ArrayList<>(taskManager.getPrioritizedTasks());

        assertTrue(prioritizedList.get(0).toString().contains("new Task test")
                && prioritizedList.get(1).toString().contains("new Task test 2")
                && prioritizedList.get(2).toString().contains("new SubTask test")
                , "Некорректная приоритизация");
    }

    @Test
    public void checkIntersections() {
        try {
            Task newTask = new Task(TaskType.TASK, "new Task test 2", "Test addNewTask description"
                    , Duration.ofMinutes(10), LocalDateTime.parse("01-01-2025 12:35", (Task.DATE_TIME_FORMATTER)));
            taskManager.addNewTask(newTask);
        } catch (UserInputException e) {
            assertTrue(e.getMessage().contains("Новая задача пересекается по времени с задачами"));
        }
    }

    @Test
    public void checkEpicStatus() {
        SubTask newSubTask = new SubTask(TaskType.SUBTASK, epic,"new SubTask test", "Test addNewTask description", Duration.ofMinutes(10), LocalDateTime.parse("01-01-2025 15:00", (Task.DATE_TIME_FORMATTER)));
        taskManager.addNewSubtask(newSubTask);

        assertEquals(TaskStatus.NEW, epic.getStatus());

        epic.getSubTasksIds()
                .forEach(id -> {
                    taskManager.getSubTask(id).setStatus(TaskStatus.DONE);
                    taskManager.updateSubtask(taskManager.getSubTask(id));
                });

        assertEquals(TaskStatus.DONE, epic.getStatus());

        subTask.setStatus(TaskStatus.NEW);
        taskManager.updateSubtask(subTask);

        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus());

        subTask.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateSubtask(subTask);

        newSubTask.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateSubtask(newSubTask);

        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus());
    }

    private int sendRequestGetStatusCode(String requestJson, String entity) {
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/" + entity);
            HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(requestJson)).build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testAddTaskHttp() throws IOException, InterruptedException {
        String taskJson = gson.toJson(httpTask);
        int statusCode = sendRequestGetStatusCode(taskJson, "tasks");

        assertEquals(201, statusCode, "Некорректный код ответа при создании задачи.");

        ArrayList<Task> tasksList = new ArrayList<>(httpTaskManager.getHttpBackedTaskManager().getTasks().values());

        assertNotNull(tasksList, "Задачи не возвращаются.");
        assertEquals(1, tasksList.size(), "Неверное количество задач.");

        assertNotNull(tasksList.stream().filter(task -> task.getName().equals("new HTTP Task test")
                && task.getDescription().equals("Test HTTP addNewTask description")
                && task.getStatus() == TaskStatus.NEW
                && task.getDuration().equals(Duration.ofMinutes(10))
                && task.getStartTime().isEqual(LocalDateTime.parse("01-01-2025 11:30", (Task.DATE_TIME_FORMATTER))))
                , "Задача не найдена.");
    }

    @Test
    public void testAddEpicHttp() throws IOException, InterruptedException {
       String taskEpic = gson.toJson(httpEpic);
        int statusCode = sendRequestGetStatusCode(taskEpic, "epics");

        assertEquals(201, statusCode, "Некорректный код ответа при создании задачи.");

        ArrayList<Epic> epicsList = new ArrayList<>(httpTaskManager.getHttpBackedTaskManager().getEpics().values());

        assertNotNull(epicsList, "Эпики не возвращаются.");
        assertEquals(1, epicsList.size(), "Неверное количество эпиков.");

        Epic createdHttpEpic = epicsList.stream().filter(newEpic -> newEpic.getName().equals("new HTTP Epic test")
                        && newEpic.getDescription().equals("Test HTTP addNewEpic description"))
                .toList()
                .getFirst();

        assertNotNull(createdHttpEpic, "Эпик создан некорректно.");

        httpSubTask = new SubTask(TaskType.SUBTASK
                , createdHttpEpic
                ,"new HTTP SubTask test"
                , "Test addNewTask description"
                , Duration.ofMinutes(10)
                , LocalDateTime.parse("01-01-2025 14:00", (Task.DATE_TIME_FORMATTER)));
        String subTaskJson = gson.toJson(httpSubTask);

        statusCode = sendRequestGetStatusCode(subTaskJson, "subtasks");

        assertEquals(201, statusCode, "Некорректный код ответа при создании подзадачи.");

        ArrayList<Task> subTasksList = new ArrayList<>(httpTaskManager.getHttpBackedTaskManager().getSubTasks().values());

        assertNotNull(subTasksList, "Подзадачи не возвращаются.");
        assertEquals(1, subTasksList.size(), "Неверное количество подзадач.");

        assertNotNull(subTasksList.stream().filter(subTask -> subTask.getName().equals("new HTTP Task test")
                        && subTask.getDescription().equals("Test HTTP addNewTask description")
                        && subTask.getStatus() == TaskStatus.NEW
                        && subTask.getDuration().equals(Duration.ofMinutes(10))
                        && subTask.getStartTime().isEqual(LocalDateTime.parse("01-01-2025 14:00", (Task.DATE_TIME_FORMATTER))))
                , "Подзадача создана некорректно.");
    }

}