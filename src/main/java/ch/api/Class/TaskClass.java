package ch.api.Class;

public class TaskClass {

    private static int counter = 0;
    int id;
    public String title;
    public String description;

    public TaskClass(String title, String description) {
        id = ++counter;
        this.title = title;
        this.description = description;
    }

    public TaskClass() {
    }
}
