import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.*;
import java.io.IOException;
import java.net.URI;
import java.net.http.*;
import java.time.*;
import com.google.gson.Gson;

class HttpTaskServerHistoryAndPrioritizedTest {
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
    void historyReflectsReads200() throws Exception {
        Task t = new Task("t","d", Status.NEW, Duration.ofMinutes(5), LocalDateTime.of(2025,1,1,9,0));
        manager.createTask(t);
        int id = manager.getTasks().getFirst().getId();

        client.send(HttpRequest.newBuilder(URI.create(BASE + "/tasks/" + id)).GET().build(),
                HttpResponse.BodyHandlers.ofString());

        HttpResponse<String> resp = client.send(
                HttpRequest.newBuilder(URI.create(BASE + "/history")).GET().build(),
                HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, resp.statusCode());
        Assertions.assertTrue(resp.body().contains("\"id\":" + id));
    }

    @Test
    void prioritizedReturnsSorted200() throws Exception {
        Task a = new Task("a","d", Status.NEW, Duration.ofMinutes(30), LocalDateTime.of(2025,1,1,10,0));
        Task b = new Task("b","d", Status.NEW, Duration.ofMinutes(15), LocalDateTime.of(2025,1,1,9,0));
        manager.createTask(a);
        manager.createTask(b);

        HttpResponse<String> resp = client.send(
                HttpRequest.newBuilder(URI.create(BASE + "/prioritized")).GET().build(),
                HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, resp.statusCode());
        Assertions.assertTrue(resp.body().indexOf("\"name\":\"b\"") < resp.body().indexOf("\"name\":\"a\""));
    }
}

