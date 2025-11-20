import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.List;

public class SubtasksHandler extends BaseHttpHandler {

    private final TaskManager manager;

    public SubtasksHandler(TaskManager manager) {
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
                        Subtask subtask = manager.getSubtask(id);
                        if (subtask == null) {
                            sendNotFound(exchange, "Not Found");
                            return;
                        }
                        sendJson(exchange, subtask, 200);
                    } else {
                        List<Subtask> subtasks = manager.getSubtasks();
                        sendJson(exchange, subtasks, 200);
                    }
                    break;
                }
                case "POST": {
                    if (parts.length == 2) {
                        String body = readBody(exchange);
                        Subtask subtask = gson.fromJson(body, Subtask.class);

                        JsonObject jsonObject = JsonParser.parseString(body).getAsJsonObject();
                        int epicId = jsonObject.get("epicId").getAsInt();

                        boolean isCreated = manager.tryCreateSubtask(subtask, epicId);

                        if (isCreated) {
                            sendText(exchange, "Подзадача добавлена!", 201);
                        } else {
                            sendHasIntersections(exchange, "Not Acceptable");
                        }
                    } else {
                        int id = Integer.parseInt(parts[2]);
                        if (manager.getSubtask(id) == null) {
                            sendNotFound(exchange, "Not Found");
                            return;
                        }
                        String body = readBody(exchange);
                        Subtask subtask = gson.fromJson(body, Subtask.class);
                        subtask.setId(id);

                        boolean isUpdated = manager.tryUpdateSubtask(subtask);

                        if (isUpdated) {
                            sendText(exchange, "Подзадача обновлена!", 201);
                        } else {
                            sendHasIntersections(exchange, "Not Acceptable");
                        }

                    }
                    break;
                }
                case "DELETE": {
                    if (parts.length == 2) {
                        manager.deleteAllSubtasks();
                        sendText(exchange, "Все удалены!", 200);
                    } else {
                        int id = Integer.parseInt(parts[2]);
                        if (manager.getSubtask(id) == null) {
                            sendNotFound(exchange, "Not Found");
                            return;
                        }
                        manager.deleteSubtask(id);
                        sendText(exchange, "Подзадача удалена!", 200);
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
