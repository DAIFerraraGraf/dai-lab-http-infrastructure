package ch.api.Class;

public class Task {

    private static int counter = 0;
    public int id;
    public String title;
    public String description;
    public int taskClassId;

    public Task(String title, String description, int taskClassId){
        id = ++counter;
        this.title = title;
        this.description = description;
        this.taskClassId = taskClassId;
    }
    public Task(){
        id = ++counter;
    }
}
