import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

class InMemoryTaskManagerTest {
    InMemoryTaskManager taskManager = new InMemoryTaskManager();
    Task task;
    Epic epic;
    Subtask subtask;
    @BeforeEach
    public void beforeEach() {
        task = new Task("t1", "test1", Status.NEW);
        taskManager.createTask(task);
        epic = new Epic("t2", "test2", Status.NEW);
        taskManager.createEpic(epic);
        int epicId = epic.getId();
        subtask = new Subtask("t3", "test3", Status.NEW);
        taskManager.createSubtask(subtask, epicId);
    }

    @Test
    public void canCreateNewTasks() {
        Assertions.assertTrue(taskManager.getTasks().contains(task));
        Assertions.assertTrue(taskManager.getEpics().contains(epic));
        Assertions.assertTrue(taskManager.getSubtasks().contains(subtask));
    }

    @Test
    public void canSearchTasksById() {
        Assertions.assertEquals(task, taskManager.getTask(task.getId()));
        Assertions.assertEquals(epic, taskManager.getEpic(epic.getId()));
        Assertions.assertEquals(subtask, taskManager.getSubtask(subtask.getId()));
    }

    @Test
    public void dontChangeAfterAdditionToManager() {
        boolean flag = true;
        if (!task.getName().equals(taskManager.getTask(task.getId()).getName())) flag = false;
        if (!task.getDescription().equals(taskManager.getTask(task.getId()).getDescription())) flag = false;
        Assertions.assertTrue(flag);
    }

    @Test
    public void dontChangeAfterAdditionToHistory () {
        taskManager.getTask(task.getId());
        assertEquals(task, taskManager.getHistory().get(0));
    }

}