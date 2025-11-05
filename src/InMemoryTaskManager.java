import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

public class InMemoryTaskManager implements TaskManager {
    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    protected final Map<Integer, Subtask> subtasks = new HashMap<>();

    private final HistoryManager historyManager = Managers.getDefaultHistory();

    protected int idCounter = 1;

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

/*методы для tasks*/

@Override
public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public void deleteAllTasks() {
        for (int id : tasks.keySet()) {
            historyManager.remove(id);
        }
        tasks.clear();
    }

    @Override
    public void deleteTask(int id) {
        historyManager.remove(id);
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
public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public void deleteAllEpics() {
        for (int id : epics.keySet()) {
            historyManager.remove(id);
        }
        for (int id : subtasks.keySet()) {
            historyManager.remove(id);
        }
        tasks.clear();
        subtasks.clear();
    }

    @Override
    public void deleteEpic(int id) {
        List<Subtask> epicSubtasks = epics.get(id).getSubtasks();
        for (Subtask subtask : epicSubtasks) {
            historyManager.remove(subtask.getId());
            subtasks.remove(subtask.getId());
        }
        historyManager.remove(id);
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
    public List<Subtask> getEpicsSubtasks(int id) {
        return epics.get(id).getSubtasks();
    }

    private void updateEpicStatus(Epic epic) {
        if (epic.getSubtasks().isEmpty()) {
            epic.setStatus(Status.NEW);
        } else {
            boolean isNew = true;
            boolean isDone = true;
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

//***********************************************************

/*методы для subtasks*/

@Override
public List<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void deleteAllSubtasks() {
        for (int id : subtasks.keySet()) {
            historyManager.remove(id);
        }
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
            historyManager.remove(id);
            subtasks.remove(id);
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
        } else if (!epics.containsKey(epicId)) {
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
