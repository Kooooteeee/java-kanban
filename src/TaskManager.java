import java.util.List;

public interface TaskManager {

    List<Task> getHistory();

    /*методы для tasks*/

    List<Task> getTasks();

    void deleteAllTasks();

    void deleteTask(int id);

    Task getTask(int id);

    boolean tryCreateTask(Task newTask);

    void createTask(Task newTask);

    boolean tryUpdateTask(Task task);

    void updateTask(Task task);

    /*методы для epics*/

    List<Epic> getEpics();

    void deleteAllEpics();

    void deleteEpic(int id);

    Epic getEpic(int id);

    boolean tryCreateEpic(Epic newEpic);

    void createEpic(Epic newEpic);

    void updateEpic(Epic epic);

    List<Subtask> getEpicsSubtasks(int id);

    /*методы для subtasks*/

    List<Subtask> getSubtasks();

    void deleteAllSubtasks();

    void deleteSubtask(int id);

    Subtask getSubtask(int id);

    int getSubtasksEpicId(Subtask subtask);

    boolean tryCreateSubtask(Subtask newSubtask, int epicId);

    void createSubtask(Subtask newSubtask, int epicId);

    boolean tryUpdateSubtask(Subtask newSubtask);

    void updateSubtask(Subtask newSubtask);

    List<Task> getPrioritizedTasks();
}
