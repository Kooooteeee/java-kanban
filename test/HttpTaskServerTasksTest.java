import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import org.junit.jupiter.api.*;
import java.io.IOException;
import java.net.URI;
import java.net.http.*;
import java.time.*;
import com.google.gson.Gson;

class HttpTaskServerTasksTest {
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
    }

    @AfterEach
    void tearDown() {
        server.stop();
    }

    @Test
    void createTask201_andVisibleInManager() throws Exception {
        Task t = new Task("t1","d", Status.NEW, Duration.ofMinutes(15), LocalDateTime.of(2025,1,1,9,0));
        HttpRequest req = HttpRequest.newBuilder(URI.create(BASE + "/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(t)))
                .header("Content-Type","application/json")
                .build();
        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(201, resp.statusCode());
        Assertions.assertEquals(1, manager.getTasks().size());
    }

    @Test
    void getTaskById200_and404WhenAbsent() throws Exception {
        Task t = new Task("t","d", Status.NEW, Duration.ofMinutes(10), LocalDateTime.of(2025,1,1,8,0));
        manager.createTask(t);
        int id = manager.getTasks().getFirst().getId();
        HttpResponse<String> ok = client.send(
                HttpRequest.newBuilder(URI.create(BASE + "/tasks/" + id)).GET().build(),
                HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, ok.statusCode());

        HttpResponse<String> nf = client.send(
                HttpRequest.newBuilder(URI.create(BASE + "/tasks/99999")).GET().build(),
                HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(404, nf.statusCode());
    }

    @Test
    void updateTask201_andDelete200() throws Exception {
        Task t = new Task("t","d", Status.NEW, Duration.ofMinutes(10), LocalDateTime.of(2025,1,1,8,0));
        manager.createTask(t);
        int id = manager.getTasks().getFirst().getId();

        Task upd = new Task("t-upd","dx", Status.IN_PROGRESS, Duration.ofMinutes(20), LocalDateTime.of(2025,1,1,8,30));
        upd.setId(id);
        HttpResponse<String> up = client.send(
                HttpRequest.newBuilder(URI.create(BASE + "/tasks/" + id))
                        .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(upd)))
                        .header("Content-Type","application/json").build(),
                HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(201, up.statusCode());

        HttpResponse<String> del = client.send(
                HttpRequest.newBuilder(URI.create(BASE + "/tasks/" + id)).DELETE().build(),
                HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, del.statusCode());
        Assertions.assertTrue(manager.getTasks().isEmpty());
    }

    @Test
    void createOverlappingTask406() throws Exception {
        Task a = new Task("a","d", Status.NEW, Duration.ofMinutes(30), LocalDateTime.of(2025,1,1,9,0));
        manager.createTask(a);
        Task b = new Task("b","d", Status.NEW, Duration.ofMinutes(30), LocalDateTime.of(2025,1,1,9,15));
        HttpResponse<String> resp = client.send(
                HttpRequest.newBuilder(URI.create(BASE + "/tasks"))
                        .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(b)))
                        .header("Content-Type","application/json").build(),
                HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(406, resp.statusCode());
        Assertions.assertEquals(1, manager.getTasks().size());
    }
}

