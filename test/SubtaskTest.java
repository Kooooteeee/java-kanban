import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;


class SubtaskTest {

    private Subtask task1;
    private Subtask task2;


    @BeforeEach
    public void beforeAll() {
        task1 = new Subtask("t1", "test1", Status.NEW);
        task2 = new Subtask("t2", "test2", Status.NEW);
        task1.setId(1);
        task2.setId(1);
    }

    @Test
    public void subtasksEqualsIfIdEquals() {
        Assertions.assertEquals(task1, task2, "Экземпляры не равны");
    }


}