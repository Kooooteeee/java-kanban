public class Subtask extends Task {
    private int epicId;

    public Subtask(String name, String description, Status status) {
        super(name, description, status);
    }


    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int id) {
        if (epicId == id) {
            System.out.println("Подзадачу нельзя сделать своим эпиком!");
            return;
        } else {
            epicId = id;
        }
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "epicId=" + epicId +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                '}';
    }
}
