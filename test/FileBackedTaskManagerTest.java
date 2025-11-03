import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {

    @TempDir Path tempDir;
    Path file;
    FileBackedTaskManager mgr;

    @BeforeEach
    void setUp() {
        file = tempDir.resolve("tasks.csv");
        mgr  = new FileBackedTaskManager(file);
    }

    @Test
    void saveEmptyManagerProducesHeaderOnlyAndLoadRestoresEmpty() throws IOException {
        mgr.save();

        String text = Files.readString(file, StandardCharsets.UTF_8);
        assertEquals("id,type,name,status,description,epic\n", text);

        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(file.toFile());
        assertTrue(loaded.getTasks().isEmpty());
        assertTrue(loaded.getEpics().isEmpty());
        assertTrue(loaded.getSubtasks().isEmpty());
    }

    @Test
    void saveAndLoadThreeEntitiesRestoreAllFieldsAndLinks() throws IOException {
        Task t = new Task("t1", "d1", Status.NEW);
        mgr.createTask(t);

        Epic e = new Epic("e1", "ed1", Status.NEW);
        mgr.createEpic(e);
        int epicId = e.getId();

        Subtask s = new Subtask("s1","sd1", Status.IN_PROGRESS);
        mgr.createSubtask(s, epicId);

        assertTrue(Files.size(file) > 0);

        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(file.toFile());

        assertEquals(1, loaded.getTasks().size());
        assertEquals(1, loaded.getEpics().size());
        assertEquals(1, loaded.getSubtasks().size());

        Task tL = loaded.getTasks().getFirst();
        Epic eL = loaded.getEpics().getFirst();
        Subtask sL = loaded.getSubtasks().getFirst();

        assertEquals(t.getId(), tL.getId());
        assertEquals(t.getName(), tL.getName());
        assertEquals(t.getDescription(), tL.getDescription());
        assertEquals(t.getStatus(), tL.getStatus());

        assertEquals(e.getId(), eL.getId());
        assertEquals(e.getName(), eL.getName());
        assertEquals(e.getStatus(), eL.getStatus());

        assertEquals(s.getId(), sL.getId());
        assertEquals(s.getEpicId(), sL.getEpicId()); // важная связка

        Task newTask = new Task("next", "n", Status.DONE);
        loaded.createTask(newTask);
        assertEquals(Math.max(Math.max(tL.getId(), eL.getId()), sL.getId()) + 1, newTask.getId());
    }
}

