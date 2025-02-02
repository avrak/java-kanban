package model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.Headers;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

import com.sun.net.httpserver.HttpHandler;
import service.FileBackedTaskManager;

public class BaseHttpHandler implements HttpHandler {
    public static final int RESP_CODE_OK = 200;
    public static final int RESP_CODE_CREATED = 201;
    public static final int RESP_CODE_NO_CONTENT = 204;
    public static final int RESP_CODE_BAD_REQUEST = 400;
    public static final int RESP_CODE_NOT_FOUND = 404;
    public static final String RESP_TEXT_NOT_FOUND_ENDPOINT = "Такого эндпоинта не существует";
    public static final String RESP_TEXT_NOT_FOUND = "Not found";
    public static final int RESP_CODE_NOT_ACCEPTABLE = 406;
    public static final int RESP_CODE_INTERNAL_ERROR = 500;
    public static final String RESP_TEXT_INTERNAL_ERROR = "Internal error";
    public static final String RESP_TEXT_EMPTY = "";

    public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private static final String TASK_ENTITIES = "tasks";
    private static final String EPIC_ENTITIES = "epics";
    private static final String SUBTASK_ENTITIES = "subtasks";
    private static final String HISTORY = "history";
    private static final String PRIORITIZED = "prioritized";

    protected Gson gson;

    FileBackedTaskManager fileBackedTaskManager;

    BaseHttpHandler(FileBackedTaskManager fileBackedTaskManager) {
        this.fileBackedTaskManager = fileBackedTaskManager;
        gson = new GsonBuilder()
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
    }

    public Endpoint getEndpoint(String requestPath, String requestMethod) {
        String[] pathParts = requestPath.split("/");

        return switch (requestMethod) {
            case "GET" -> switch (pathParts[1]) {
                case TASK_ENTITIES -> switch (pathParts.length) {
                    case 3 -> Endpoint.GET_TASK;
                    case 2 -> Endpoint.GET_TASKS;
                    default -> Endpoint.UNKNOWN;
                };
                case SUBTASK_ENTITIES -> switch (pathParts.length) {
                    case 3 -> Endpoint.GET_SUBTASK;
                    case 2 -> Endpoint.GET_SUBTASKS;
                    default -> Endpoint.UNKNOWN;
                };
                case EPIC_ENTITIES -> switch (pathParts.length) {
                    case 3 -> Endpoint.GET_EPIC;
                    case 2 -> Endpoint.GET_EPICS;
                    case 4 -> Endpoint.GET_EPIC_SUBTASKS;
                    default -> Endpoint.UNKNOWN;
                };
                case HISTORY -> Endpoint.HISTORY;
                case PRIORITIZED -> Endpoint.PRIORITIZED;
                default -> Endpoint.UNKNOWN;
            };
            case "POST" -> switch (pathParts[1]) {
                case TASK_ENTITIES -> switch (pathParts.length) {
                    case 2 -> Endpoint.ADD_TASK;
                    case 3 -> Endpoint.UPDATE_TASK;
                    default -> Endpoint.UNKNOWN;
                };
                case SUBTASK_ENTITIES -> switch (pathParts.length) {
                    case 2 -> Endpoint.ADD_SUBTASK;
                    case 3 -> Endpoint.UPDATE_SUBTASK;
                    default -> Endpoint.UNKNOWN;
                };
                case EPIC_ENTITIES -> switch (pathParts.length) {
                    case 2 -> Endpoint.ADD_EPIC;
                    case 3 -> Endpoint.UPDATE_EPIC;
                    default -> Endpoint.UNKNOWN;
                };
                default -> Endpoint.UNKNOWN;
            };
            case "DELETE" -> switch (pathParts[1]) {
                case TASK_ENTITIES -> switch (pathParts.length) {
                    case 2 -> Endpoint.DELETE_TASKS;
                    case 3 -> Endpoint.DELETE_TASK;
                    default ->  Endpoint.UNKNOWN;
                };
                case SUBTASK_ENTITIES -> switch (pathParts.length) {
                    case 2 -> Endpoint.DELETE_SUBTASKS;
                    case 3 -> Endpoint.DELETE_SUBTASK;
                    default ->  Endpoint.UNKNOWN;
                };
                case EPIC_ENTITIES -> switch (pathParts.length) {
                    case 2 -> Endpoint.DELETE_EPICS;
                    case 3 -> Endpoint.DELETE_EPIC;
                    default -> Endpoint.UNKNOWN;
                };
                default -> Endpoint.UNKNOWN;
            };
            default -> Endpoint.UNKNOWN;
        };
    }

    public Optional<Integer> getTaskId(HttpExchange exchange) {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        try {
            return Optional.of(Integer.parseInt(pathParts[2]));
        } catch (NumberFormatException exception) {
            return Optional.empty();
        }
    }

    public void sendText(HttpExchange exchange, String responseString) throws IOException {
        Headers headers = exchange.getResponseHeaders();

        headers.add("Content-Type", "application/json; charset=utf-8");
        byte[] response = responseString.getBytes(DEFAULT_CHARSET);

        try (OutputStream os = exchange.getResponseBody()) {
            exchange.sendResponseHeaders(RESP_CODE_OK, response.length);
            os.write(response);
        }
        exchange.close();
    }

    public void sendTaskProcessed(HttpExchange exchange) throws IOException {
        Headers headers = exchange.getResponseHeaders();

        headers.add("Content-Type", "text/plain; charset=utf-8");

        try (OutputStream os = exchange.getResponseBody()) {
            exchange.sendResponseHeaders(RESP_CODE_CREATED, 0);
            os.write("".getBytes(DEFAULT_CHARSET));
        }
        exchange.close();
    }

    public void sendNotFound(HttpExchange exchange, String message) throws IOException {
        Headers headers = exchange.getResponseHeaders();

        headers.add("Content-Type", "text/plain; charset=utf-8");

        if (message.isEmpty()) message = RESP_TEXT_NOT_FOUND;

        byte[] response = message.getBytes(DEFAULT_CHARSET);

        try (OutputStream os = exchange.getResponseBody()) {
            exchange.sendResponseHeaders(RESP_CODE_NOT_FOUND, response.length);
            os.write(response);
        }
        exchange.close();
    }

    public void sendHasInteractions(HttpExchange exchange, String message) throws IOException {
        Headers headers = exchange.getResponseHeaders();

        headers.add("Content-Type", "text/plain; charset=utf-8");

        byte[] response = message.getBytes(DEFAULT_CHARSET);

        try (OutputStream os = exchange.getResponseBody()) {
            exchange.sendResponseHeaders(RESP_CODE_BAD_REQUEST, response.length);
            os.write(response);
        }
        exchange.close();
    }

    public void sendInternalError(HttpExchange exchange) throws IOException {
        Headers headers = exchange.getResponseHeaders();

        headers.add("Content-Type", "text/plain; charset=utf-8");

        byte[] response = RESP_TEXT_INTERNAL_ERROR.getBytes(DEFAULT_CHARSET);

        try (OutputStream os = exchange.getResponseBody()) {
            exchange.sendResponseHeaders(RESP_CODE_INTERNAL_ERROR, response.length);
            os.write(response);
        }
        exchange.close();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

    }
}
