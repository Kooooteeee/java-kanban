import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

public class Epic extends Task {
    private List<Subtask> subtasks;
    protected LocalDateTime endTime;

    public Epic(String name, String description, Status status) {
        super(name, description, status, null, null);
        endTime = null;
    }

    public void setTime() {
        List<Subtask> timed = subtasks.stream()
                .filter(s -> s.getStartTime() != null && s.getDuration() != null) // или под задачу
                .toList();

        if (timed.isEmpty()) {
            resetTime();
            return;
        }

        startTime = timed.stream()
                .map(Subtask::getStartTime)
                .min(Comparator.naturalOrder())
                .orElse(null);

        endTime = timed.stream()
                .map(Subtask::getEndTime)
                .max(Comparator.naturalOrder())
                .orElse(null);

        duration = timed.stream()
                .map(Subtask::getDuration)
                .reduce(Duration.ZERO, Duration::plus);
    }

    public void resetTime() {
        startTime = null;
        endTime = null;
        duration = null;
    }

    public List<Subtask> getSubtasks() {
        return subtasks;
    }

    public void setSubtasks(List<Subtask> subtasks) {
        this.subtasks = subtasks;
    }

    public void deleteSubtask(Subtask subtask) {
        subtasks.remove(subtask);
    }

    public void addSubtask(Subtask subtask) {
        subtasks.add(subtask);
    }

    @Override
    public String toString() {
        return "Epic{" +
                "subtasks=" + subtasks +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                '}';
    }
}
