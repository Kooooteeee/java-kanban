import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
public class InMemoryHistoryManager implements HistoryManager {

    private final List<Task> history = new ArrayList<>();
    private final HashMap<Integer, Node<Task>> smartHistory = new HashMap<>();
    public Node<Task> head;
    public Node<Task> tail;
    private int size = 0;

    public void removeNode(Node<Task> node) {
        if (node == head && node == tail) {    // единственный элемент
            head = null;
            tail = null;
        } else if(node == head) {
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

    public void linkLast(Task task) {
        Node<Task> node = new Node<>(task);
        if(size == 0) {
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
        size++;
    }

    public void getTasks() {
        history.clear();
        Node<Task> workNode = head;
        while (workNode != null) {
            history.add(workNode.data);
            workNode = workNode.next;
        }
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
            if (size == 1) {
                smartHistory.remove(id);
                head = null;
                tail = null;
                size--;
            } else if (size == 0) {
                System.out.println("История пуста!");
            } else {
                removeNode(smartHistory.get(id));
                smartHistory.remove(id);
                size--;
            }
        } else {
            System.out.println("Такой записи в истории просмотров нет!");
        }
    }

    @Override
    public List<Task> getHistory() {
        getTasks();
        return history;
    }
}
class Node <Task> {

    public Task data;
    public Node<Task> next;
    public Node<Task> prev;

    public Node(Task data) {
        this.data = data;
        this.next = null;
        this.prev = null;
    }
}
