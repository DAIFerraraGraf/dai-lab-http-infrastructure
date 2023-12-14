package ch.api.Controller;

import ch.api.Class.Task;
import ch.api.Class.TaskClass;
import io.javalin.http.Context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TaskClassController {
    private List<TaskClass> taskClasses = Collections.synchronizedList(new ArrayList<>());

    public TaskClassController(){
        taskClasses.add(new TaskClass("Class1", "test"));
        taskClasses.add(new TaskClass("Class2", "test"));
    }
    public void getAll(Context ctx){
        ctx.json(new ArrayList<>(taskClasses));
    }

    public void getOne(Context ctx){
        int id = Integer.parseInt(ctx.pathParam("id"));
        TaskClass taskClass = taskClasses.stream().filter(t -> t.id == id).findFirst().orElse(null);
        if (taskClass == null){
            ctx.status(404);
            ctx.result("Not found");
            return;
        }
        ctx.json(taskClass);
    }

    public void create(Context ctx){
        TaskClass taskClass = ctx.bodyAsClass(TaskClass.class);
        taskClasses.add(taskClass);
        ctx.status(201);
    }

    public void delete(Context ctx) {
        int id = Integer.parseInt(ctx.pathParam("id"));
        taskClasses.removeIf(task -> task.id == id);
        ctx.status(204);
    }

    public void update(Context ctx) {
        int id = Integer.parseInt(ctx.pathParam("id"));
        TaskClass newTaskClass = ctx.bodyAsClass(TaskClass.class);
        taskClasses.removeIf(task -> task.id == id);
        taskClasses.add(newTaskClass);
        ctx.status(200);
    }
}
