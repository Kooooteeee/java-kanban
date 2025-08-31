import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

class EpicTest {

    private Epic task1;
    private Epic task2;


    @BeforeEach
    public void beforeAll() {
        task1 = new Epic("t1", "test1", Status.NEW);
        task2 = new Epic("t2", "test2", Status.NEW);
        task1.setId(1);
        task2.setId(1);
    }

    @Test
    public void epicsEqualsIfIdEquals() {
        Assertions.assertEquals(task1, task2, "Экземпляры не равны");
    }


  
}