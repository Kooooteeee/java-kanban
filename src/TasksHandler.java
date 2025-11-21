import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.List;

public class TasksHandler extends BaseHttpHandler {

    private final TaskManager manager;

    public TasksHandler(TaskManager manager) {
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
                        Task task = manager.getTask(id);
                        if (task == null) {
                            sendNotFound(exchange, "Not found");
                            return;
                        } else sendJson(exchange, task, 200);
                    } else {
                        List<Task> tasks = manager.getTasks();
                        sendJson(exchange, tasks, 200);
                    }
                    break;
                }
                case "POST": {
                    if (parts.length == 2) {
                        String body = readBody(exchange);
                        Task task = gson.fromJson(body, Task.class);

                        boolean isCreated = manager.tryCreateTask(task);

                        if (isCreated) {
                            sendText(exchange, "Задача создана!", 201);
                        } else {
                            sendHasIntersections(exchange, "Not Acceptable");
                        }
                    } else {
                        int id = Integer.parseInt(parts[2]);
                        if (manager.getTask(id) == null) {
                            sendNotFound(exchange, "Not found");
                            return;
                        }
                        String body = readBody(exchange);
                        Task task = gson.fromJson(body, Task.class);
                        task.setId(id);
                        boolean isUpdated = manager.tryUpdateTask(task);
                        if (isUpdated) {
                            sendText(exchange, "Задача обновлена!", 201);
                        } else {
                            sendHasIntersections(exchange, "Not Acceptable");
                        }
                    }
                    break;
                }
                case "DELETE": {
                    if (parts.length == 2) {
                        manager.deleteAllTasks();
                        sendText(exchange, "Все задачи удалены!", 200);
                    } else {
                        int id = Integer.parseInt(parts[2]);
                        Task task = manager.getTask(id);
                        if (task == null) {
                            sendNotFound(exchange, "Not found");
                            return;
                        }
                        manager.deleteTask(id);
                        sendText(exchange, "Задача удалена", 200);
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
