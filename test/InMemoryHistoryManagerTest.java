import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;

class InMemoryHistoryManagerTest {

    HistoryManager historyManager = new InMemoryHistoryManager();
    Task task;

    @Test
    public void testOfSizeLimitAndAddingTasks() {
        int idCounter = 0;
        for (int i = 0; i < 11; i++) {
            task = new Task("t " + i , "test " + i, Status.NEW);
            task.setId(idCounter);
            idCounter++;
            historyManager.addTask(task);
        }
        //System.out.println(historyManager.getHistory());
        Assertions.assertEquals(10, historyManager.getHistory().size());
    }

    @Test
    public void  testOfUniqueValues() {
        int idCounter = 0;
        for (int i = 0; i < 11; i++) {
            task = new Task("t " + i , "test " + i, Status.NEW);
            task.setId(idCounter);
            if(i%2 == 0) {
                idCounter++;
            }
            historyManager.addTask(task);
        }
        //System.out.println(historyManager.getHistory());
        Assertions.assertEquals(6, historyManager.getHistory().size());
    }

    @Test
    public void testOfRemovingValues() {
        int idCounter = 0;
        for (int i = 0; i < 11; i++) {
            task = new Task("t " + i , "test " + i, Status.NEW);
            task.setId(idCounter);
            historyManager.addTask(task);
            idCounter++;
        }

        historyManager.remove(1);
        historyManager.remove(2);
        historyManager.remove(10);
        System.out.println(historyManager.getHistory());
        Assertions.assertEquals(7, historyManager.getHistory().size());
    }




}