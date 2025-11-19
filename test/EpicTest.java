import org.junit.jupiter.api.*;
import java.time.*;

class EpicTest {

    InMemoryTaskManager taskManager;
    Epic epic;

    @BeforeEach
    void setUp() {
        taskManager = new InMemoryTaskManager();
        epic = new Epic("E", "D", Status.NEW);
        taskManager.createEpic(epic);
    }

    @Test
    void allNew() {
        Subtask s1 = new Subtask("s1","d", Status.NEW, Duration.ofHours(1), LocalDateTime.of(2025,1,1,9,0));
        Subtask s2 = new Subtask("s2","d", Status.NEW, Duration.ofHours(2), LocalDateTime.of(2025,1,1,11,0));
        taskManager.createSubtask(s1, epic.getId());
        taskManager.createSubtask(s2, epic.getId());
        Assertions.assertEquals(Status.NEW, taskManager.getEpic(epic.getId()).getStatus());
    }

    @Test
    void allDone() {
        Subtask s1 = new Subtask("s1","d", Status.NEW, Duration.ofHours(1), LocalDateTime.of(2025,1,1,9,0));
        Subtask s2 = new Subtask("s2","d", Status.NEW, Duration.ofHours(2), LocalDateTime.of(2025,1,1,11,0));
        taskManager.createSubtask(s1, epic.getId());
        taskManager.createSubtask(s2, epic.getId());

        Subtask s1m = taskManager.getSubtask(s1.getId());
        s1m.setStatus(Status.DONE);
        taskManager.updateSubtask(s1m);

        Subtask s2m = taskManager.getSubtask(s2.getId());
        s2m.setStatus(Status.DONE);
        taskManager.updateSubtask(s2m);

        Assertions.assertEquals(Status.DONE, taskManager.getEpic(epic.getId()).getStatus());
    }

    @Test
    void mixNewAndDone() {
        Subtask s1 = new Subtask("s1","d", Status.NEW, Duration.ofHours(1), LocalDateTime.of(2025,1,1,9,0));
        Subtask s2 = new Subtask("s2","d", Status.NEW, Duration.ofHours(2), LocalDateTime.of(2025,1,1,11,0));
        taskManager.createSubtask(s1, epic.getId());
        taskManager.createSubtask(s2, epic.getId());

        Subtask s2m = taskManager.getSubtask(s2.getId());
        s2m.setStatus(Status.DONE);
        taskManager.updateSubtask(s2m);

        Assertions.assertEquals(Status.IN_PROGRESS, taskManager.getEpic(epic.getId()).getStatus());
    }

    @Test
    void hasInProgress() {
        Subtask s1 = new Subtask("s1","d", Status.NEW, Duration.ofHours(1), LocalDateTime.of(2025,1,1,9,0));
        Subtask s2 = new Subtask("s2","d", Status.NEW, Duration.ofHours(2), LocalDateTime.of(2025,1,1,11,0));
        taskManager.createSubtask(s1, epic.getId());
        taskManager.createSubtask(s2, epic.getId());

        Subtask s1m = taskManager.getSubtask(s1.getId());
        s1m.setStatus(Status.IN_PROGRESS);
        taskManager.updateSubtask(s1m);

        Assertions.assertEquals(Status.IN_PROGRESS, taskManager.getEpic(epic.getId()).getStatus());
    }

    @Test
    void epicTimeCalculatedFromSubtasks() {
        Subtask a = new Subtask("a","d", Status.NEW, Duration.ofMinutes(90), LocalDateTime.of(2025,1,1,8,30));
        Subtask b = new Subtask("b","d", Status.NEW, Duration.ofHours(2), LocalDateTime.of(2025,1,1,10,0));
        taskManager.createSubtask(a, epic.getId());
        taskManager.createSubtask(b, epic.getId());

        Epic e = taskManager.getEpic(epic.getId());
        Assertions.assertEquals(LocalDateTime.of(2025,1,1,8,30), e.getStartTime());
        Assertions.assertEquals(LocalDateTime.of(2025,1,1,12,0), e.getEndTime());
        Assertions.assertEquals(Duration.ofMinutes(90).plus(Duration.ofHours(2)), e.getDuration());
    }
}
