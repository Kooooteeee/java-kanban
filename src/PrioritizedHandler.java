import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

public class PrioritizedHandler extends BaseHttpHandler {

    private final TaskManager manager;

    public PrioritizedHandler (TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle (HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();

        try {
            if (method.equals("GET")) {
                sendJson(exchange, manager.getPrioritizedTasks(), 200);
                return;
            }
            sendMethodNotFound(exchange);
        } catch (Exception e) {
            sendHasInternalServerError(exchange, "Internal Server Error");
        }
    }
}
