import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final Path path;

    private boolean loading = false;

    public FileBackedTaskManager(Path path) {
        this.path = path;
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        if(!loading) save();
    }

    @Override
    public void deleteTask(int id) {
        super.deleteTask(id);
        if(!loading) save();
    }

    @Override
    public void createTask(Task newTask) {
        super.createTask(newTask);
        if(!loading) save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        if(!loading) save();
    }

//***********************************************************

    /*методы для epics*/

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        if(!loading) save();
    }

    @Override
    public void deleteEpic(int id) {
        super.deleteEpic(id);
        if(!loading) save();
    }

    @Override
    public void createEpic(Epic newEpic) {
        super.createEpic(newEpic);
        if(!loading) save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        if(!loading) save();
    }

//***********************************************************

    /*методы для subtasks*/

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        if(!loading) save();
    }

    @Override
    public void deleteSubtask(int id) {
        super.deleteSubtask(id);
        if(!loading) save();
    }

    @Override
    public void createSubtask(Subtask newSubtask, int epicId) {
        super.createSubtask(newSubtask, epicId);
        if(!loading) save();
    }

    @Override
    public void updateSubtask(Subtask newSubtask) {
        super.updateSubtask(newSubtask);
        if(!loading) save();
    }

    public void save() {
        try {
            Path tmp = Files.createTempFile(path.getParent(), "tasks-", ".csv");

            StringBuilder sb = new StringBuilder();
            sb.append("id,type,name,status,description,epic\n");
            for (Task t : super.getTasks())    sb.append(toString(t));
            for (Epic e : super.getEpics())    sb.append(toString(e));
            for (Subtask s : super.getSubtasks())  sb.append(toString(s));

            Files.writeString(tmp, sb.toString(), StandardCharsets.UTF_8);

            Files.move(tmp, path, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения файла");
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) throws IOException {
        FileBackedTaskManager fileManager = new FileBackedTaskManager(file.toPath());
        fileManager.loading = true;
        if (Files.exists(file.toPath())) {
            try (BufferedReader br = new BufferedReader(
                    new FileReader(String.valueOf(file.toPath()), StandardCharsets.UTF_8))) {
                br.readLine();
                int maxId = 0;
                while (br.ready()) {
                    Task task = fromString(br.readLine());
                    if (task.getId() > maxId) maxId = task.getId();
                    if (task instanceof Subtask) {
                        fileManager.idCounter = task.getId();
                        fileManager.createSubtask(((Subtask) task), ((Subtask) task).getEpicId());
                    } else if (task instanceof Epic) {
                        fileManager.idCounter = task.getId();
                        fileManager.createEpic(((Epic) task));
                    } else {
                        fileManager.idCounter = task.getId();
                        fileManager.createTask(task);
                    }
                }
                fileManager.idCounter = maxId + 1;
            } catch (IOException e) {
                throw new ManagerSaveException("Файл не найден");
            }
        }
        return fileManager;
    }

    public String toString(Task task) {
        final String type = (task instanceof Subtask) ? "SUBTASK" :
                (task instanceof Epic) ? "EPIC" : "TASK";
        final String strEnd = (task instanceof Subtask) ?
                String.valueOf( ((Subtask) task).getEpicId()) : "";

        return String.format("%d,%s,%s,%s,%s,%s\n",
                task.getId(), type, task.getName(), task.getStatus(), task.getDescription(), strEnd);
    }

    public static Task fromString(String value) {
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
