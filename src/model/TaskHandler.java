package model;
import com.sun.net.httpserver.HttpExchange;
import service.FileBackedTaskManager;

import java.io.IOException;
import java.util.ArrayList;

public class TaskHandler extends BaseHttpHandler {

    public TaskHandler(FileBackedTaskManager fileBackedTaskManager) {
        super(fileBackedTaskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());

        switch (endpoint) {
            case GET_TASK:
                handleGetTask(exchange);
                break;
            case GET_TASKS:
                handleGetTasks(exchange);
                break;
            case ADD_TASK:
                handleAddTask(exchange);
                break;
            case DELETE_TASK:
                handleDeleteTask(exchange);
                break;
            case DELETE_TASKS:
                handleDeleteTasks(exchange);
                break;
            default:
                sendNotFound(exchange, RESP_TEXT_NOT_FOUND_ENDPOINT);
        }
    }

    private void handleGetTask(HttpExchange exchange) throws IOException {
        Task task = fileBackedTaskManager.getTask(getTaskId(exchange).get());

        if (task != null) {
            sendText(exchange, gson.toJson(task));
        } else {
            sendNotFound(exchange, RESP_TEXT_EMPTY);
        }
    }

    private void handleGetTasks(HttpExchange exchange) throws IOException {
        try {
            ArrayList<Task> tasksList = new ArrayList<>(fileBackedTaskManager.getTasks().values());
            String response = gson.toJson(tasksList);
            sendText(exchange, response);
        } catch (Exception e) {
            sendInternalError(exchange);
            throw new IOException();
        }
    }

    public void handleAddTask(HttpExchange exchange) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes(), DEFAULT_CHARSET);

        try {
            Task jsonTask = gson.fromJson(body, Task.class);
            if (jsonTask.getTaskId() == 0) {
                fileBackedTaskManager.updateTask(jsonTask);
            } else {
                Task task = new Task(jsonTask.getType(), jsonTask.getName(), jsonTask.getDescription(), jsonTask.getDuration(), jsonTask.getStartTime());
                fileBackedTaskManager.addNewTask(task);
            }
            sendTaskProcessed(exchange);
        } catch (UserInputException e) {
            sendHasInteractions(exchange, e.getMessage());
        } catch (Exception e) {
            sendInternalError(exchange);
        }
    }

    public void handleDeleteTask(HttpExchange exchange) throws IOException {
        Task task = fileBackedTaskManager.getTasks().get(getTaskId(exchange).get());

        if (task != null) {
            fileBackedTaskManager.deleteTask(task.getTaskId());
            sendTaskProcessed(exchange);
        } else {
            sendNotFound(exchange, RESP_TEXT_EMPTY);
        }
    }

    public void handleDeleteTasks(HttpExchange exchange) throws IOException {
        fileBackedTaskManager.deleteTasks();
        sendTaskProcessed(exchange);
    }

}
