import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryTaskManager implements TaskManager {
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();

    private final HistoryManager historyManager = Managers.getDefaultHistory();

    private int idCounter = 1;

    public ArrayList<Task> getHistory() {
        return historyManager.getHistory();
    }

/*методы для tasks*/
@Override
public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public void deleteAllTasks() {
        tasks.clear();
    }

    @Override
    public void deleteTask(int id) {
        tasks.remove(id);
    }

    @Override
    public Task getTask(int id) {
        if (tasks.containsKey(id)) {
            historyManager.addTask(tasks.get(id));
            return tasks.get(id);
        } else {
            System.out.println("Такой задачи нет :(");
            return null;
        }
    }

    @Override
    public void createTask(Task newTask) {
        if (tasks.containsValue(newTask)) {
            System.out.println("Такая задача уже есть!");
        } else {
            newTask.setId(idCounter);
            newTask.setStatus(Status.NEW);
            tasks.put(newTask.getId(), newTask);
            //System.out.println("Id задачи: " + idCounter);
            idCounter++;
        }
    }

    @Override
    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        } else {
            System.out.println("Такой задачи нет :(");
        }
    }
//***********************************************************

/*методы для epics*/
@Override
public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public void deleteAllEpics() {
        tasks.clear();
        subtasks.clear();
    }

    @Override
    public void deleteEpic(int id) {
        ArrayList<Subtask> epicSubtasks = epics.get(id).getSubtasks();
        for(Subtask subtask : epicSubtasks) {
            subtasks.remove(subtask.getId());
        }
        epics.remove(id);
    }

    @Override
    public Epic getEpic(int id) {
        if (epics.containsKey(id)) {
            historyManager.addTask(epics.get(id));
            return epics.get(id);
        } else {
            System.out.println("Такого эпика нет :(");
            return null;
        }

    }

    @Override
    public void createEpic(Epic newEpic) {
        if (epics.containsValue(newEpic)) {
            System.out.println("Такой эпик уже есть!");
        } else {
            newEpic.setId(idCounter);
            newEpic.setStatus(Status.NEW);
            newEpic.setSubtasks(new ArrayList<>());
            epics.put(newEpic.getId(), newEpic);
            //System.out.println("Id эпика: " + idCounter);
            idCounter++;
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            updateEpicStatus(epics.get(epic.getId()));
            epics.put(epic.getId(), epic);
        } else {
            System.out.println("Такого эпика нет :(");
        }
    }

    @Override
    public ArrayList<Subtask> getEpicsSubtasks(int id) {
        return epics.get(id).getSubtasks();
    }
//***********************************************************

/*методы для subtasks*/
@Override
public ArrayList<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void deleteAllSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.setStatus(Status.NEW);
        }
    }

    @Override
    public void deleteSubtask(int id) {
        if (subtasks.containsKey(id)) {
            epics.get(subtasks.get(id).getEpicId()).deleteSubtask(subtasks.get(id));
            updateEpicStatus(epics.get(subtasks.get(id).getEpicId()));
        } else {
            System.out.println("Такой подзадачи нет :(");
        }
    }

    @Override
    public Subtask getSubtask(int id) {
        if (subtasks.containsKey(id)) {
            historyManager.addTask(subtasks.get(id));
            return subtasks.get(id);
        } else {
            System.out.println("Такой подзадачи нет :(");
            return null;
        }
    }

    @Override
    public int getSubtasksEpicId(Subtask subtask) {
        return subtask.getEpicId();
    }

    @Override
    public void createSubtask(Subtask newSubtask, int epicId) {
        if (subtasks.containsValue(newSubtask)) {
            System.out.println("Такая подзадача уже есть!");
        }
        else if (!epics.containsKey(epicId)) {
            System.out.println("Эпик не найден");
        } else {
            newSubtask.setId(idCounter);
            newSubtask.setEpicId(epicId);
            newSubtask.setStatus(Status.NEW);
            epics.get(newSubtask.getEpicId()).addSubtask(newSubtask);
            updateEpicStatus(epics.get(newSubtask.getEpicId()));
            subtasks.put(newSubtask.getId(), newSubtask);
            //System.out.println("Id подзадачи: " + idCounter);
            idCounter++;
        }
    }

    @Override
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
