package model;
import com.sun.net.httpserver.HttpExchange;
import service.FileBackedTaskManager;

import java.io.IOException;
import java.util.Objects;

public class PrioritizedHandler extends BaseHttpHandler {

    public PrioritizedHandler(FileBackedTaskManager fileBackedTaskManager) {
        super(fileBackedTaskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());

        if (Objects.requireNonNull(endpoint) == Endpoint.PRIORITIZED) {
            handleGetPrioritized(exchange);
        } else {
            sendNotFound(exchange, RESP_TEXT_NOT_FOUND_ENDPOINT);
        }
    }

    private void handleGetPrioritized(HttpExchange exchange) throws IOException {
        sendText(exchange, gson.toJson(fileBackedTaskManager.getPrioritizedTasks()));
    }

}
