import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    protected final Map<Integer, Subtask> subtasks = new HashMap<>();

    private final HistoryManager historyManager = Managers.getDefaultHistory();

    protected final Set<Task> prioritized = new TreeSet<>(Comparator.comparing(Task::getStartTime,
            Comparator.nullsLast(Comparator.naturalOrder())).thenComparingInt(Task::getId));

    // сравниваем по startTime, null в конец(на всякий случай на будущее)
    // в случае если время равно или оно null, срвниваем по id

    protected int idCounter = 1;

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritized);
    }

    private boolean isTasksIntersects(Task task1, Task task2) {
        if (task1.getStartTime() == null || task1.getEndTime() == null ||
                task2.getStartTime() == null || task2.getEndTime() == null) return false;
        return task1.getStartTime().isBefore(task2.getEndTime()) && task2.getStartTime().isBefore(task1.getEndTime());
    }

    private boolean isTaskIntersectsWithOther(Task task) {
        return prioritized.stream()
                .filter(t -> t.getId() != task.getId())
                .filter(t -> t.getStartTime() != null && t.getEndTime() != null)
                .anyMatch(t -> isTasksIntersects(task, t));
    }

    /*методы для tasks*/

    @Override
    public List<Task> getTasks() {
            return new ArrayList<>(tasks.values());
        }

    @Override
    public void deleteAllTasks() {
        tasks.keySet().forEach(historyManager::remove);
        prioritized.removeAll(tasks.values());
        tasks.clear();
    }

    @Override
    public void deleteTask(int id) {
        prioritized.remove(tasks.get(id));
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
    public boolean tryCreateTask(Task newTask) {
        if (isTaskIntersectsWithOther(newTask) || tasks.containsValue(newTask)) {
            return false;
        } else {
            int before = getTasks().size();
            createTask(newTask);
            int after = getTasks().size();
            return after != before;
        }
    }

    @Override
    public void createTask(Task newTask) {
        if (tasks.containsValue(newTask)) {
            System.out.println("Такая задача уже есть!");
        } else if (isTaskIntersectsWithOther(newTask)) {
            System.out.println("Задача пересекается по времени с другой!");
        } else {
            newTask.setId(idCounter);
            newTask.setStatus(Status.NEW);
            tasks.put(newTask.getId(), newTask);
            //System.out.println("Id задачи: " + idCounter);
            if (newTask.getStartTime() != null) {
                prioritized.add(newTask);
            }
            idCounter++;
        }
    }

    @Override
    public boolean tryUpdateTask(Task task) {
        if (isTaskIntersectsWithOther(task)) return false;
        updateTask(task);
        return true;
    }

    @Override
    public void updateTask(Task task) {
        if (isTaskIntersectsWithOther(task)) {
            System.out.println("Задача пересекается по времени с другой!");
        } else if (tasks.containsKey(task.getId())) {
            prioritized.remove(tasks.get(task.getId()));
            tasks.put(task.getId(), task);
            if (task.getStartTime() != null) {
                prioritized.add(task);
            }
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
        epics.keySet().forEach(historyManager::remove);
        subtasks.keySet().forEach(historyManager::remove);
        prioritized.removeAll(subtasks.values());

        epics.clear();
        subtasks.clear();
    }

    @Override
    public void deleteEpic(int id) {
        epics.get(id).getSubtasks().forEach(subtask -> {
            historyManager.remove(subtask.getId());
            prioritized.remove(subtask);
            subtasks.remove(subtask.getId());
        });

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
    public boolean tryCreateEpic(Epic newEpic) {
        int before = getEpics().size();
        createEpic(newEpic);
        int after = getEpics().size();
        return after != before;
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
        return Optional.ofNullable(epics.get(id))
                .map(Epic::getSubtasks)
                .orElseGet(Collections::emptyList);

    }

    protected void updateEpicStatus(Epic epic) {
        if (epic.getSubtasks().isEmpty()) {
            epic.setStatus(Status.NEW);
            epic.resetTime();
            return;
        }
        boolean isNew = epic.getSubtasks().stream().allMatch(s -> s.getStatus() == Status.NEW);
        boolean isDone = epic.getSubtasks().stream().allMatch((s -> s.getStatus() == Status.DONE));


        if (isDone) epic.setStatus(Status.DONE);
        else if (isNew) epic.setStatus(Status.NEW);
        else epic.setStatus(Status.IN_PROGRESS);
        epic.setTime();

    }

//***********************************************************

/*методы для subtasks*/

@Override
public List<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void deleteAllSubtasks() {
        subtasks.keySet().forEach(historyManager::remove);
        prioritized.removeAll(subtasks.values());
        subtasks.clear();
        epics.values().forEach(epic -> {
            epic.getSubtasks().clear();
        updateEpicStatus(epic);
        });
    }

    @Override
    public void deleteSubtask(int id) {
        if (subtasks.containsKey(id)) {
            epics.get(subtasks.get(id).getEpicId()).deleteSubtask(subtasks.get(id));
            updateEpicStatus(epics.get(subtasks.get(id).getEpicId()));
            historyManager.remove(id);
            prioritized.remove(subtasks.get(id));
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
    public boolean tryCreateSubtask(Subtask newSubtask, int epicId) {
        if (isTaskIntersectsWithOther(newSubtask) || subtasks.containsValue(newSubtask)) {
            return false;
        } else {
            int before = getSubtasks().size();
            createSubtask(newSubtask, epicId);
            int after = getSubtasks().size();
            return after != before;
        }
    }

    @Override
    public void createSubtask(Subtask newSubtask, int epicId) {
        if (subtasks.containsValue(newSubtask)) {
            System.out.println("Такая подзадача уже есть!");
        } else if (isTaskIntersectsWithOther(newSubtask)) {
            System.out.println("Подзадача пересекается по времени с другой!");
        } else if (!epics.containsKey(epicId)) {
            System.out.println("Эпик не найден");
        } else {
            newSubtask.setId(idCounter);
            newSubtask.setEpicId(epicId);
            newSubtask.setStatus(Status.NEW);
            epics.get(newSubtask.getEpicId()).addSubtask(newSubtask);
            updateEpicStatus(epics.get(newSubtask.getEpicId()));
            subtasks.put(newSubtask.getId(), newSubtask);
            if (newSubtask.getStartTime() != null) {
                prioritized.add(newSubtask);
            }
            idCounter++;
        }
    }

    @Override
    public boolean tryUpdateSubtask(Subtask newSubtask) {
        if (isTaskIntersectsWithOther(newSubtask)) return false;
        updateSubtask(newSubtask);
        return true;
    }

    @Override
    public void updateSubtask(Subtask newSubtask) {
        if (!subtasks.containsKey(newSubtask.getId())) {
            System.out.println("Такой подзадачи нет :(");
            return;
        } else if (isTaskIntersectsWithOther(newSubtask)) {
            System.out.println("Подзадача пересекается по времени с другой!");
            return;
        }
        prioritized.remove(subtasks.get(newSubtask.getId()));
        epics.get(newSubtask.getEpicId()).deleteSubtask(subtasks.get(newSubtask.getId()));
        epics.get(newSubtask.getEpicId()).addSubtask(newSubtask);
        updateEpicStatus(epics.get(newSubtask.getEpicId()));
        subtasks.put(newSubtask.getId(), newSubtask);
        if (newSubtask.getStartTime() != null) {
            prioritized.add(newSubtask);
        }
    }

//***********************************************************
}
