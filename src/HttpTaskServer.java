import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public final class HttpTaskServer implements AutoCloseable {
    private final TaskManager manager;
    private final int port;
    private final AtomicBoolean running = new AtomicBoolean(false);
    private final HttpServer server;

    public static final int DEFAULT_PORT = 8080;

    public HttpTaskServer(TaskManager manager) throws IOException {
        this(manager, DEFAULT_PORT);
    }

    public HttpTaskServer(TaskManager manager, int port) throws IOException {
        this.manager = Objects.requireNonNull(manager, "manager");
        this.port = port;
        this.server = HttpServer.create(new InetSocketAddress(port), 0);
        registerContexts();
    }

    private void registerContexts() {
        server.createContext("/tasks", new TasksHandler(manager));
        server.createContext("/subtasks", new SubtasksHandler(manager));
        server.createContext("/epics", new EpicsHandler(manager));
        server.createContext("/history", new HistoryHandler(manager));
        server.createContext("/prioritized", new PrioritizedHandler(manager));
    }

    public void start() {
        if (running.compareAndSet(false, true)) {
            server.start();
            System.out.println("Сервер запущен " + getBaseUrl());
        }
    }

    public void stop() {
        if (running.compareAndSet(true, false)) {
            server.stop(0);
        }
    }

    public boolean isRunning() {
        return running.get();
    }

    public int getPort() {
        return server.getAddress().getPort();
    }

    public String getBaseUrl() {
        return "http://localhost:" + getPort();
    }

    @Override public void close() {
        stop();
    }
    
    public static void main(String[] args) throws IOException {
        TaskManager manager = Managers.getDefault();
        HttpTaskServer http = new HttpTaskServer(manager, DEFAULT_PORT);
        http.start();
        // http.stop();
    }
}
