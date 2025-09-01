import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

class ManagersTest {

    @Test
    void getDefaultShouldReturnInitializedTaskManager() {
        TaskManager taskManager = Managers.getDefault();
        Assertions.assertNotNull(taskManager);
    }

    @Test
    void getDefaultHistoryShouldReturnInitializedHistoryManager() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        Assertions.assertNotNull(historyManager);
    }
}
