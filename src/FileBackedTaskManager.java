import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final Path path;

    public FileBackedTaskManager(Path path) {
        this.path = path;
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteTask(int id) {
        super.deleteTask(id);
        save();
    }

    @Override
    public void createTask(Task newTask) {
        super.createTask(newTask);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

//***********************************************************

    /*методы для epics*/

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public void deleteEpic(int id) {
        super.deleteEpic(id);
        save();
    }

    @Override
    public void createEpic(Epic newEpic) {
        super.createEpic(newEpic);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

//***********************************************************

    /*методы для subtasks*/

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }

    @Override
    public void deleteSubtask(int id) {
        super.deleteSubtask(id);
        save();
    }

    @Override
    public void createSubtask(Subtask newSubtask, int epicId) {
        super.createSubtask(newSubtask, epicId);
        save();
    }

    @Override
    public void updateSubtask(Subtask newSubtask) {
        super.updateSubtask(newSubtask);
        save();
    }

    private void save() {
        try {
            Path target = path.toAbsolutePath();
            Path dir = target.getParent();
            if (dir != null) {
                Files.createDirectories(dir);
            }

            StringBuilder sb = new StringBuilder();
            sb.append("id,type,name,status,description,epic\n");
            for (Task t : super.getTasks())    sb.append(toString(t)).append('\n');
            for (Epic e : super.getEpics())    sb.append(toString(e)).append('\n');
            for (Subtask s : super.getSubtasks())  sb.append(toString(s)).append('\n');

            Files.writeString(target, sb.toString(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения файла");
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) throws IOException {
        FileBackedTaskManager fileManager = new FileBackedTaskManager(file.toPath());
        if (Files.exists(file.toPath())) {
            try (BufferedReader br = new BufferedReader(
                    new FileReader(String.valueOf(file.toPath()), StandardCharsets.UTF_8))) {
                br.readLine();
                int maxId = 0;
                while (br.ready()) {
                    Task task = fromString(br.readLine());
                    if (task.getId() > maxId) maxId = task.getId();
                    if (task instanceof Subtask) {
                        fileManager.subtasks.put(task.getId(), ((Subtask) task));
                        fileManager.epics.get(((Subtask) task).getEpicId()).addSubtask(((Subtask) task));
                    } else if (task instanceof Epic) {
                        Epic epic = (Epic) task;
                        epic.setSubtasks(new ArrayList<>());
                        fileManager.epics.put(task.getId(), ((Epic) task));
                    } else {
                        fileManager.tasks.put(task.getId(), task);
                    }
                }
                fileManager.idCounter = maxId + 1;
                for (Epic e : fileManager.epics.values()) {
                    fileManager.updateEpicStatus(e); //на всякий случай пересчитваем состояния эпиков, не уверен, что это нужно
                }
            } catch (IOException e) {
                throw new ManagerSaveException("Файл не найден");
            }
        }
        return fileManager;
    }

    private String toString(Task task) {
        final String type = (task instanceof Subtask) ? "SUBTASK" :
                (task instanceof Epic) ? "EPIC" : "TASK";
        final String strEnd = (task instanceof Subtask) ?
                String.valueOf(((Subtask) task).getEpicId()) : "";

        return String.format("%d,%s,%s,%s,%s,%s",
                task.getId(), type, task.getName(), task.getStatus(), task.getDescription(), strEnd);
    }

    private static Task fromString(String value) {
        String[] str = value.split(",");
        int id = Integer.parseInt(str[0]);
        Task task;
        if (str[1].equals("TASK")) {
            task = new Task(str[2], str[4], Status.valueOf(str[3]));
            task.setId(id);
            return task;
        } else if (str[1]. equals("EPIC")) {
            task = new Epic(str[2], str[4], Status.valueOf(str[3]));
            task.setId(id);
            return task;
        } else {
            Subtask subTask;
            subTask = new Subtask(str[2], str[4], Status.valueOf(str[3]));
            subTask.setId(id);
            subTask.setEpicId(Integer.parseInt(str[5]));
            return subTask;
        }
    }
}
