import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private HashMap<Integer, Subtask> subtasks = new HashMap<>();

    private int idCounter = 1;

/*методы для tasks*/
    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    public void deleteAllTasks() {
        tasks.clear();
    }

    public void deleteTask(int id) {
        tasks.remove(id);
    }

    public Task getTask(int id) {
        if (tasks.containsKey(id)) {
            return tasks.get(id);
        } else {
            System.out.println("Такой задачи нет :(");
            return null;
        }
    }

    public void createTask(Task newTask) {
        if (tasks.containsValue(newTask)) {
            System.out.println("Такая задача уже есть!");
        } else {
            newTask.setId(idCounter);
            newTask.setStatus(Status.NEW);
            tasks.put(newTask.getId(), newTask);
            idCounter++;
        }
    }

    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        } else {
            System.out.println("Такой задачи нет :(");
        }
    }
//***********************************************************

/*методы для epics*/
    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    public void deleteAllEpics() {
        tasks.clear();
        subtasks.clear();
    }

    public void deleteEpic(int id) {
        ArrayList<Subtask> epicSubtasks = epics.get(id).getSubtasks();
        for(Subtask subtask : epicSubtasks) {
            subtasks.remove(subtask.getId());
        }
        epics.remove(id);
    }

    public Epic getEpic(int id) {
        if (epics.containsKey(id)) {
            return epics.get(id);
        } else {
            System.out.println("Такого эпика нет :(");
            return null;
        }

    }

    public void createEpic(Epic newEpic) {
        if (epics.containsValue(newEpic)) {
            System.out.println("Такой эпик уже есть!");
        } else {
            newEpic.setId(idCounter);
            newEpic.setStatus(Status.NEW);
            newEpic.setSubtasks(new ArrayList<>());
            epics.put(newEpic.getId(), newEpic);
            idCounter++;
        }
    }

    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            updateEpicStatus(epics.get(epic.getId()));
            epics.put(epic.getId(), epic);
        } else {
            System.out.println("Такого эпика нет :(");
        }
    }

    private void updateEpicStatus(Epic epic) {
        if (epic.getSubtasks().isEmpty()) {
            epic.setStatus(Status.NEW);
        } else {
            boolean isNew = true;
            boolean isDone = true;
            boolean isInProgress = true;
            for (Subtask subtask : epic.getSubtasks()) {
                if (subtask.getStatus() == Status.NEW) {
                    isDone = false;
                } if (subtask.getStatus() == Status.DONE) {
                    isNew = false;
                }
            }

            if (isDone) epic.setStatus(Status.DONE);
            else if (isNew) epic.setStatus(Status.NEW);
            else epic.setStatus(Status.IN_PROGRESS);
        }
    }

    public ArrayList<Subtask> getEpicsSubtasks(int id) {
        return epics.get(id).getSubtasks();
    }
//***********************************************************

/*методы для subtasks*/
    public ArrayList<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    public void deleteAllSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.setStatus(Status.NEW);
        }
    }

    public void deleteSubtask(int id) {
        if (subtasks.containsKey(id)) {
            epics.get(subtasks.get(id).getEpicId()).deleteSubtask(subtasks.get(id));
            updateEpicStatus(epics.get(subtasks.get(id).getEpicId()));
        } else {
            System.out.println("Такой подзадачи нет :(");
        }
    }

    public Subtask getSubtask(int id) {
        if (subtasks.containsKey(id)) {
            return subtasks.get(id);
        } else {
            System.out.println("Такой подзадачи нет :(");
            return null;
        }
    }

    public int getSubtasksEpicId(Subtask subtask) {
        return subtask.getEpicId();
    }

    public void createSubtask(Subtask newSubtask) {
        if (subtasks.containsValue(newSubtask)) {
            System.out.println("Такая подзадача уже есть!");
        } else {
            newSubtask.setId(idCounter);
            newSubtask.setStatus(Status.NEW);
            epics.get(newSubtask.getEpicId()).addSubtask(newSubtask);
            updateEpicStatus(epics.get(newSubtask.getEpicId()));
            subtasks.put(newSubtask.getId(), newSubtask);
            idCounter++;
        }
    }

    public void updateSubtask(Subtask newSubtask) {
        if (subtasks.containsKey(newSubtask.getId())) {
            Subtask subtask = subtasks.get(newSubtask.getId());
            epics.get(subtask.getEpicId()).deleteSubtask(subtask);
            epics.get(subtask.getEpicId()).addSubtask(newSubtask);
            updateEpicStatus(epics.get(newSubtask.getEpicId()));
            subtasks.put(subtask.getId(), subtask);
        } else {
            System.out.println("Такой подзадачи нет :(");
        }
    }
//***********************************************************
}
