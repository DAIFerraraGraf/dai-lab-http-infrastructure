package ch.api.Class;

public class Task {
    private static Integer count = 0;
    public int id;
    public String title;
    public String description;

    public Task(String title, String description){
        id = count++;
        this.title = title;
        this.description = description;
    }
    public Task(){
    }
}
