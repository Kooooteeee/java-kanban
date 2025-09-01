import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;

class InMemoryHistoryManagerTest {

    InMemoryHistoryManager historyManager = new InMemoryHistoryManager();

    @Test
    public void testOfSizeLimit() {
        for (int i = 0; i < 15; i++) {
            historyManager.addTask(new Task("t " + i , "test " + i, Status.NEW));
        }
        Assertions.assertEquals(10, historyManager.getHistory().size());
    }




}