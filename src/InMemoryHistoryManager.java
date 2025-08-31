import java.util.ArrayList;
public class InMemoryHistoryManager implements HistoryManager{

    private final ArrayList<Task> history = new ArrayList<>();

    @Override
    public void addTask(Task task) {
        if (history.size() > 10) {
            history.removeFirst();
            history.add(task);
        } else {
            history.add(task);
        }
    }

    @Override
    public ArrayList<Task> getHistory() {
        return history;
    }
}
