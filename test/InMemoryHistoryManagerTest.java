import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;

class InMemoryHistoryManagerTest {

    HistoryManager historyManager = new InMemoryHistoryManager();
    Task task;

    @Test
    public void testOfUnlimitSizeAndAddingTasks() {
        int idCounter = 0;
        for (int i = 0; i < 15; i++) {
            task = new Task("t " + i , "test " + i, Status.NEW);
            task.setId(idCounter);
            idCounter++;
            historyManager.addTask(task);
        }
        task = new Task("t " + 5 , "test " + 5, Status.NEW);
        task.setId(5);
        historyManager.addTask(task);
        System.out.println(historyManager.getHistory());
        Assertions.assertEquals(15, historyManager.getHistory().size());
    }

    @Test
    public void  testOfUniqueValues() {
        int idCounter = 0;
        for (int i = 0; i < 15; i++) {
            task = new Task("t " + i , "test " + i, Status.NEW);
            task.setId(idCounter);
            if(i%2 == 0) {
                idCounter++;
            }
            historyManager.addTask(task);
        }
        //System.out.println(historyManager.getHistory());
        Assertions.assertEquals(8, historyManager.getHistory().size());
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

        historyManager.remove(0);
        historyManager.remove(2);
        historyManager.remove(10);
        //System.out.println(historyManager.getHistory());
        Assertions.assertEquals(8, historyManager.getHistory().size());
    }




}