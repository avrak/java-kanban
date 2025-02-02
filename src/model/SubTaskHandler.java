package model;
import com.sun.net.httpserver.HttpExchange;
import service.FileBackedTaskManager;

import java.io.IOException;
import java.util.ArrayList;

public class SubTaskHandler extends BaseHttpHandler {

    public SubTaskHandler(FileBackedTaskManager fileBackedTaskManager) {
        super(fileBackedTaskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());

        switch (endpoint) {
            case GET_SUBTASK:
                handleGetSubTask(exchange);
                break;
            case GET_SUBTASKS:
                handleGetSubTasks(exchange);
                break;
            case ADD_SUBTASK:
                handleAddSubTask(exchange);
                break;
            case DELETE_SUBTASK:
                handleDeleteSubTask(exchange);
                break;
            case DELETE_SUBTASKS:
                handleDeleteSubTasks(exchange);
                break;
            default:
                sendText(exchange, RESP_TEXT_NOT_FOUND_ENDPOINT);
        }
    }

    private void handleGetSubTask(HttpExchange exchange) throws IOException {
        SubTask subTask = fileBackedTaskManager.getSubTask(getTaskId(exchange).get());

        if (subTask != null) {
            sendText(exchange, gson.toJson(subTask));
        } else {
            sendNotFound(exchange, RESP_TEXT_EMPTY);
        }
    }

    private void handleGetSubTasks(HttpExchange exchange) throws IOException {
        try {
            ArrayList<SubTask> tasksList = new ArrayList<>(fileBackedTaskManager.getSubTasks().values());
            String response = gson.toJson(tasksList);
            sendText(exchange, response);
        } catch (Exception e) {
            sendInternalError(exchange);
            throw new IOException();
        }
    }

    public void handleAddSubTask(HttpExchange exchange) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes(), DEFAULT_CHARSET);

        try {
            SubTask jsonSubTask = gson.fromJson(body, SubTask.class);
            if (jsonSubTask.getTaskId() == 0) {
                fileBackedTaskManager.updateSubtask(jsonSubTask);
            } else {
                SubTask subTask = new SubTask(jsonSubTask.getType(),
                        fileBackedTaskManager.getEpic(jsonSubTask.getEpicId()),
                        jsonSubTask.getName(),
                        jsonSubTask.getDescription(),
                        jsonSubTask.getDuration(),
                        jsonSubTask.getStartTime());
                fileBackedTaskManager.addNewSubtask(subTask);
            }
            sendTaskProcessed(exchange);
        } catch (UserInputException e) {
            sendHasInteractions(exchange, e.getMessage());
        } catch (Exception e) {
            sendInternalError(exchange);
        }
    }

    public void handleDeleteSubTask(HttpExchange exchange) throws IOException {
        SubTask subTask = fileBackedTaskManager.getSubTask(getTaskId(exchange).get());
        if (subTask != null) {
            fileBackedTaskManager.deleteTask(subTask.getTaskId());
            sendTaskProcessed(exchange);
        } else {
            sendNotFound(exchange, RESP_TEXT_EMPTY);
        }

    }

    public void handleDeleteSubTasks(HttpExchange exchange) throws IOException {
        fileBackedTaskManager.deleteSubTasks();
        sendTaskProcessed(exchange);
    }

}
