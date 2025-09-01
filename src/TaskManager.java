import java.util.ArrayList;
import java.util.List;

public interface TaskManager {
    /*методы для tasks*/
    List<Task> getTasks();

    void deleteAllTasks();

    void deleteTask(int id);

    Task getTask(int id);

    void createTask(Task newTask);

    void updateTask(Task task);

    /*методы для epics*/
    List<Epic> getEpics();

    void deleteAllEpics();

    void deleteEpic(int id);

    Epic getEpic(int id);

    void createEpic(Epic newEpic);

    void updateEpic(Epic epic);

    List<Subtask> getEpicsSubtasks(int id);

    /*методы для subtasks*/
    List<Subtask> getSubtasks();

    void deleteAllSubtasks();

    void deleteSubtask(int id);

    Subtask getSubtask(int id);

    int getSubtasksEpicId(Subtask subtask);

    void createSubtask(Subtask newSubtask, int epicId);

    void updateSubtask(Subtask newSubtask);
}
