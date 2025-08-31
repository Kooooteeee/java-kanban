import java.util.ArrayList;

public interface TaskManager {
    /*методы для tasks*/
    ArrayList<Task> getTasks();

    void deleteAllTasks();

    void deleteTask(int id);

    Task getTask(int id);

    void createTask(Task newTask);

    void updateTask(Task task);

    /*методы для epics*/
    ArrayList<Epic> getEpics();

    void deleteAllEpics();

    void deleteEpic(int id);

    Epic getEpic(int id);

    void createEpic(Epic newEpic);

    void updateEpic(Epic epic);

    default void updateEpicStatus(Epic epic) {
        if (epic.getSubtasks().isEmpty()) {
            epic.setStatus(Status.NEW);
        } else {
            boolean isNew = true;
            boolean isDone = true;
            boolean isInProgress = true;
            for (Subtask subtask : epic.getSubtasks()) {
                if (subtask.getStatus() == Status.NEW) {
                    isDone = false;
                }
                if (subtask.getStatus() == Status.DONE) {
                    isNew = false;
                }
            }

            if (isDone) epic.setStatus(Status.DONE);
            else if (isNew) epic.setStatus(Status.NEW);
            else epic.setStatus(Status.IN_PROGRESS);
        }
    }

    ArrayList<Subtask> getEpicsSubtasks(int id);

    /*методы для subtasks*/
    ArrayList<Subtask> getSubtasks();

    void deleteAllSubtasks();

    void deleteSubtask(int id);

    Subtask getSubtask(int id);

    int getSubtasksEpicId(Subtask subtask);

    void createSubtask(Subtask newSubtask, int epicId);

    void updateSubtask(Subtask newSubtask);
}
