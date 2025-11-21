import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.*;
import java.io.IOException;
import java.net.URI;
import java.net.http.*;
import java.time.*;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

class HttpTaskServerSubtasksTest {
    private InMemoryTaskManager manager;
    private HttpTaskServer server;
    private HttpClient client;
    private static final String BASE = "http://localhost:8080";

    Gson gson = new GsonBuilder()
            .registerTypeAdapter(Duration.class, new DurationSecondsAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    @BeforeEach
    void setUp() throws IOException {
        manager = new InMemoryTaskManager();
        server = new HttpTaskServer(manager);
        server.start();
        client = HttpClient.newHttpClient();
        manager.createEpic(new Epic("E","ed", Status.NEW));
    }

    @AfterEach
    void tearDown() {
        server.stop();
    }

    @Test
    void createSubtask201_getById200_delete200() throws Exception {
        int epicId = manager.getEpics().getFirst().getId();
        Subtask s = new Subtask("S","sd", Status.NEW, Duration.ofMinutes(20), LocalDateTime.of(2025,1,1,10,0));
        JsonObject payload = gson.fromJson(gson.toJson(s), JsonObject.class);
        payload.addProperty("epicId", epicId);

        HttpResponse<String> create = client.send(
                HttpRequest.newBuilder(URI.create(BASE + "/subtasks"))
                        .POST(HttpRequest.BodyPublishers.ofString(payload.toString()))
                        .header("Content-Type","application/json").build(),
                HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(201, create.statusCode());
        int id = manager.getSubtasks().getFirst().getId();

        HttpResponse<String> get = client.send(
                HttpRequest.newBuilder(URI.create(BASE + "/subtasks/" + id)).GET().build(),
                HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, get.statusCode());

        HttpResponse<String> del = client.send(
                HttpRequest.newBuilder(URI.create(BASE + "/subtasks/" + id)).DELETE().build(),
                HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, del.statusCode());
        Assertions.assertTrue(manager.getSubtasks().isEmpty());
    }

    @Test
    void createSubtaskOverlap406() throws Exception {
        int epicId = manager.getEpics().getFirst().getId();
        Subtask a = new Subtask("A","d", Status.NEW, Duration.ofMinutes(30), LocalDateTime.of(2025,1,1,11,0));
        a.setEpicId(epicId);
        manager.createSubtask(a, epicId);

        Subtask b = new Subtask("B","d", Status.NEW, Duration.ofMinutes(30), LocalDateTime.of(2025,1,1,11,15));
        JsonObject payload = gson.fromJson(gson.toJson(b), JsonObject.class);
        payload.addProperty("epicId", epicId);

        HttpResponse<String> resp = client.send(
                HttpRequest.newBuilder(URI.create(BASE + "/subtasks"))
                        .POST(HttpRequest.BodyPublishers.ofString(payload.toString()))
                        .header("Content-Type","application/json").build(),
                HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(406, resp.statusCode());
        Assertions.assertEquals(1, manager.getSubtasks().size());
    }

    @Test
    void getEpicsSubtasks200() throws Exception {
        int epicId = manager.getEpics().getFirst().getId();
        Subtask s = new Subtask("S","sd", Status.NEW, Duration.ofMinutes(10), LocalDateTime.of(2025,1,1,12,0));
        s.setEpicId(epicId);
        manager.createSubtask(s, epicId);

        HttpResponse<String> resp = client.send(
                HttpRequest.newBuilder(URI.create(BASE + "/epics/" + epicId + "/subtasks")).GET().build(),
                HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, resp.statusCode());
    }
}

