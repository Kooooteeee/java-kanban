import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

public class InMemoryHistoryManager implements HistoryManager {

    private final HashMap<Integer, Node<Task>> smartHistory = new HashMap<>();
    private Node<Task> head;
    private Node<Task> tail;

    private void removeNode(Node<Task> node) {
        if (node == head && node == tail) {    // единственный элемент
            head = null;
            tail = null;
        } else if (node == head) {
            if (head.next != null) head.next.prev = null;
            head = head.next;
        } else if (node == tail) {
            if (tail.prev != null) tail.prev.next = null;
            tail = tail.prev;
        } else {
            node.prev.next = node.next;
            node.next.prev = node.prev;
        }
        node.prev = null;
        node.next = null;
    }

    private void linkLast(Task task) {
        Node<Task> node = new Node<>(task);
        if (smartHistory.isEmpty()) {
            node.prev = null;
            node.next = null;
            head = node;
        } else {
            tail.next = node;
            node.prev = tail;
            node.next = null;
        }
        tail = node;
        smartHistory.put(task.getId(), node);
    }

    public List<Task> getTasks() {
        List<Task> history = new ArrayList<>();
        Node<Task> workNode = head;
        while (workNode != null) {
            history.add(workNode.data);
            workNode = workNode.next;
        }
        return history;
    }

    @Override
    public void addTask(Task task) {
        if (smartHistory.containsKey(task.getId())) {
            remove(task.getId());
            linkLast(task);
        } else {
            linkLast(task);
        }

    }

    @Override
    public void remove(int id) {
        if (smartHistory.containsKey(id)) {
            if (smartHistory.size() == 1) {
                smartHistory.remove(id);
                head = null;
                tail = null;
            } else if (smartHistory.isEmpty()) {
                System.out.println("История пуста!");
            } else {
                removeNode(smartHistory.get(id));
                smartHistory.remove(id);
            }
        } else {
            System.out.println("Такой записи в истории просмотров нет!");
        }
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }
}