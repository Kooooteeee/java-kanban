import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.*;
import java.io.IOException;
import java.net.URI;
import java.net.http.*;
import java.time.Duration;
import java.time.LocalDateTime;

import com.google.gson.Gson;

class HttpTaskServerEpicsTest {
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
    void createEpic201_andGetById200_andDelete200() throws Exception {
        Epic e = new Epic("E","ed", Status.NEW);
        HttpResponse<String> create = client.send(
                HttpRequest.newBuilder(URI.create(BASE + "/epics"))
                        .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(e)))
                        .header("Content-Type","application/json").build(),
                HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(201, create.statusCode());
        int id = manager.getEpics().getFirst().getId();

        HttpResponse<String> get = client.send(
                HttpRequest.newBuilder(URI.create(BASE + "/epics/" + id)).GET().build(),
                HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, get.statusCode());

        HttpResponse<String> del = client.send(
                HttpRequest.newBuilder(URI.create(BASE + "/epics/" + id)).DELETE().build(),
                HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, del.statusCode());
        Assertions.assertTrue(manager.getEpics().isEmpty());
    }
}

