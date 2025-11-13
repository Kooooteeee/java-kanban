import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.*;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest extends TaskManagerTest<TaskManager> {

    @TempDir Path dir;
    Path file;

    @Override
    protected TaskManager createManager() {
        file = dir.resolve("tasks.csv");
        return new FileBackedTaskManager(file);
    }

    @Test
    void saveEmptyManagerHeaderAndLoadBack() throws IOException {
        Files.deleteIfExists(file);
        mgr.deleteAllTasks();
        String text = Files.readString(file, StandardCharsets.UTF_8);
        assertEquals("id,type,name,status,description,epic,startTime,duration\n", text);

        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(file.toFile());
        assertTrue(loaded.getTasks().isEmpty());
        assertTrue(loaded.getEpics().isEmpty());
        assertTrue(loaded.getSubtasks().isEmpty());
    }

    @Test
    void saveLoadRestoresFieldsLinksIdsPrioritizedAndEpicTime() throws IOException {
        Task t = new Task("t","d", Status.NEW, Duration.ofMinutes(10), LocalDateTime.of(2025,1,1,9,0));
        mgr.createTask(t);
        Epic e = new Epic("e","ed", Status.NEW);
        mgr.createEpic(e);
        int epicId = e.getId();
        Subtask s = new Subtask("s","sd", Status.IN_PROGRESS, Duration.ofMinutes(30), LocalDateTime.of(2025,1,1,10,0));
        mgr.createSubtask(s, epicId);

        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(file.toFile());

        assertEquals(1, loaded.getTasks().size());
        assertEquals(1, loaded.getEpics().size());
        assertEquals(1, loaded.getSubtasks().size());

        Task tL = loaded.getTasks().getFirst();
        Epic eL = loaded.getEpics().getFirst();
        Subtask sL = loaded.getSubtasks().getFirst();

        assertEquals(t.getId(), tL.getId());
        assertEquals(t.getStartTime(), tL.getStartTime());
        assertEquals(t.getDuration(), tL.getDuration());

        assertEquals(e.getId(), eL.getId());
        assertEquals(s.getEpicId(), sL.getEpicId());

        Task next = new Task("next","n", Status.DONE, Duration.ofMinutes(5), LocalDateTime.of(2025,1,1,8,0));
        loaded.createTask(next);
        int expectedNextId = Math.max(Math.max(tL.getId(), eL.getId()), sL.getId()) + 1;
        assertEquals(expectedNextId, next.getId());

        var pr = loaded.getPrioritizedTasks();
        assertEquals(next.getId(), pr.get(0).getId());

        assertEquals(LocalDateTime.of(2025,1,1,10,0), eL.getStartTime());
        assertEquals(LocalDateTime.of(2025,1,1,10,30), eL.getEndTime());
        assertEquals(Duration.ofMinutes(30), eL.getDuration());
    }
}
