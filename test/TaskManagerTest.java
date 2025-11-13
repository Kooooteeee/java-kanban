import org.junit.jupiter.api.*;
import java.time.*;
import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {

    protected T mgr;

    protected abstract T createManager();

    @BeforeEach
    void init() {
        mgr = createManager();
    }

    @Test
    void subtaskMustBelongToExistingEpicAndAffectsEpicStatusAndTime() {
        Epic epic = new Epic("E", "D", Status.NEW);
        mgr.createEpic(epic);
        int epicId = epic.getId();

        Subtask s1 = new Subtask("s1", "d", Status.NEW,
                Duration.ofMinutes(20), LocalDateTime.of(2025, 1, 2, 9, 50)); // конец 10:10
        Subtask s2 = new Subtask("s2", "d", Status.NEW,
                Duration.ofMinutes(20), LocalDateTime.of(2025, 1, 2, 10, 10)); // старт = конец s1

        mgr.createSubtask(s1, epicId);
        mgr.createSubtask(s2, epicId);

        assertEquals(2, mgr.getEpicsSubtasks(epicId).size());

        Subtask s1m = mgr.getSubtask(s1.getId());
        s1m.setStatus(Status.DONE);
        mgr.updateSubtask(s1m);
        assertEquals(Status.IN_PROGRESS, mgr.getEpic(epicId).getStatus());

        Subtask s2m = mgr.getSubtask(s2.getId());
        s2m.setStatus(Status.DONE);
        mgr.updateSubtask(s2m);
        assertEquals(Status.DONE, mgr.getEpic(epicId).getStatus());

        Epic e = mgr.getEpic(epicId);

        LocalDateTime expectedStart = s1.getStartTime();
        LocalDateTime expectedEnd   = s2.getEndTime();
        Duration expectedDuration   = s1.getDuration().plus(s2.getDuration());

        assertEquals(expectedStart, e.getStartTime());
        assertEquals(expectedEnd,   e.getEndTime());
        assertEquals(expectedDuration, e.getDuration());
    }

}

