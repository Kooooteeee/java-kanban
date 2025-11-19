import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.List;

public class EpicsHandler extends BaseHttpHandler {
    private final TaskManager manager;

    public EpicsHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        String[] parts = path.split("/");

        try {
            switch (method) {
                case "GET": {
                    if (parts.length == 3) {
                        int id = Integer.parseInt(parts[2]);
                        Epic epic = manager.getEpic(id);
                        if (epic == null) {
                            sendNotFound(exchange, "Not found");
                            return;
                        } else sendJson(exchange, epic, 200);
                    } else if (parts.length == 4) {
                        int id = Integer.parseInt(parts[2]);
                        Epic epic = manager.getEpic(id);
                        if (epic == null) {
                            sendNotFound(exchange, "Not found");
                            return;
                        }
                        List<Subtask> epicsSubtasks = manager.getEpicsSubtasks(id);
                        sendJson(exchange, epicsSubtasks, 200);
                    } else {
                        List<Epic> epics = manager.getEpics();
                        sendJson(exchange, epics, 200);
                    }
                    break;
                }
                case "POST": {

                    String body = readBody(exchange);
                    Epic epic = gson.fromJson(body, Epic.class);
                    if (manager.hasIntersections(epic)) {
                        sendHasIntersections(exchange, "Not Acceptable");
                        return;
                    }
                    int before = manager.getEpics().size();
                    manager.createEpic(epic);
                    int after = manager.getEpics().size();
                    if (after != before) {
                        sendText(exchange, "Эпик создан!", 201);
                    } else {
                        sendHasIntersections(exchange, "Not Acceptable");
                    }
                    break;
                }
                case "DELETE": {
                    if (parts.length == 2) {
                        manager.deleteAllEpics();
                        sendText(exchange, "Все эпики удалены!", 200);
                    } else {
                        int id = Integer.parseInt(parts[2]);
                        Epic epic = manager.getEpic(id);
                        if (epic == null) {
                            sendNotFound(exchange, "Not found");
                            return;
                        }
                        manager.deleteEpic(id);
                        sendText(exchange, "Эпик удален!", 200);
                    }
                    break;
                }
                default:
                    sendMethodNotFound(exchange);
            }
        } catch (Exception e) {
            sendHasInternalServerError(exchange, "Internal Server Error");
        }
    }

}
