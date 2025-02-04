package model;
import com.sun.net.httpserver.HttpExchange;
import service.FileBackedTaskManager;

import java.io.IOException;
import java.util.ArrayList;

public class EpicHandler extends BaseHttpHandler {

    public EpicHandler(FileBackedTaskManager fileBackedTaskManager) {
        super(fileBackedTaskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());

        switch (endpoint) {
            case GET_EPIC:
                handleGetEpic(exchange);
                break;
            case GET_EPICS:
                handleGetEpics(exchange);
                break;
            case ADD_EPIC:
                handleAddEpic(exchange);
                break;
            case GET_EPIC_SUBTASKS:
                handleGetEpicSubtasks(exchange);
                break;
            case DELETE_EPIC:
                handleDeleteEpic(exchange);
                break;
            case DELETE_EPICS:
                handleDeleteEpics(exchange);
                break;
            default:
                sendText(exchange, RESP_TEXT_NOT_FOUND_ENDPOINT);
        }
    }

    private void handleGetEpic(HttpExchange exchange) throws IOException {
        Epic epic = fileBackedTaskManager.getEpic(getTaskId(exchange).get());
        if (epic != null) {
            sendText(exchange, gson.toJson(epic));
        } else {
            sendNotFound(exchange, RESP_TEXT_EMPTY);
        }
    }

    private void handleGetEpics(HttpExchange exchange) throws IOException {
        try {
            ArrayList<Epic> epicsList = new ArrayList<>(fileBackedTaskManager.getEpics().values());
            String response = gson.toJson(epicsList);
            sendText(exchange, response);
        } catch (Exception e) {
            sendInternalError(exchange);
            throw new IOException();
        }
    }

    public void handleAddEpic(HttpExchange exchange) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes(), DEFAULT_CHARSET);

        try {
            Epic jsonEpic = gson.fromJson(body, Epic.class);
            if (jsonEpic.getTaskId() == 0) {
                fileBackedTaskManager.updateEpic(jsonEpic);
            } else {
                Epic epic = new Epic(jsonEpic.getType(), jsonEpic.getName(), jsonEpic.getDescription());
                fileBackedTaskManager.addNewEpic(epic);
            }
            sendTaskProcessed(exchange);
        } catch (UserInputException e) {
            sendHasInteractions(exchange, e.getMessage());
        } catch (Exception e) {
            sendInternalError(exchange);
        }

    }

    public void handleGetEpicSubtasks(HttpExchange exchange) throws IOException {
        Epic epic = fileBackedTaskManager.getEpic(getTaskId(exchange).get());

        if (epic != null) {
            ArrayList<SubTask> subTasksList = new ArrayList<>();
            epic.getSubTasksIds().forEach(id -> subTasksList.add(fileBackedTaskManager.getSubTask(id)));
            sendText(exchange, gson.toJson(subTasksList));
        } else {
            sendNotFound(exchange, RESP_TEXT_EMPTY);
        }
    }

    public void handleDeleteEpic(HttpExchange exchange) throws IOException {
        Epic epic = fileBackedTaskManager.getEpics().get(getTaskId(exchange).get());

        if (epic != null) {
            fileBackedTaskManager.deleteTask(epic.getTaskId());
            sendTaskProcessed(exchange);
        } else {
            sendNotFound(exchange, RESP_TEXT_EMPTY);
        }
    }

    public void handleDeleteEpics(HttpExchange exchange) throws IOException {
        fileBackedTaskManager.deleteEpics();
        sendTaskProcessed(exchange);
    }
}
