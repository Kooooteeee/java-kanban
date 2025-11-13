import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.time.*;

class InMemoryHistoryManagerTest {

    HistoryManager hm;
    Task t;

    @BeforeEach
    void setUp() {
        hm = new InMemoryHistoryManager();
    }

    @Test
    void emptyHistory() {
        assertTrue(hm.getHistory().isEmpty());
    }

    @Test
    void dedupAndOrder() {
        for (int i = 0; i < 5; i++) {
            t = new Task("t"+i, "d"+i, Status.NEW, Duration.ofMinutes(1), LocalDateTime.now());
            t.setId(i);
            hm.addTask(t);
        }
        Task dup = new Task("t2", "d2", Status.NEW, Duration.ofMinutes(1), LocalDateTime.now());
        dup.setId(2);
        hm.addTask(dup);
        assertEquals(5, hm.getHistory().size());
        assertEquals(dup, hm.getHistory().getLast());
    }

    @Test
    void removeFirstMiddleLast() {
        for (int i = 0; i < 4; i++) {
            t = new Task("t"+i, "d"+i, Status.NEW, Duration.ofMinutes(1), LocalDateTime.now());
            t.setId(i);
            hm.addTask(t);
        }
        hm.remove(0);
        hm.remove(2);
        hm.remove(3);
        assertEquals(1, hm.getHistory().size());
        assertEquals(1, hm.getHistory().getFirst().getId());
    }
}
