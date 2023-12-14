package ch.api.Controller;

import ch.api.Class.Task;
import io.javalin.http.Context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class TaskController {
    private List<Task> tasks = Collections.synchronizedList(new ArrayList<>());
    public TaskController(){

        tasks.add(new Task("DAI", "reviser le TE",1));
        tasks.add(new Task("POO", "Faire les devoirs pour le 11.10",1));
        tasks.add(new Task("PST", "finir le projet",2));
        tasks.add(new Task("Math3", "avancer sur les exercices",2));
    }

    public void getAll(Context ctx){
        ctx.json(new ArrayList<>(tasks));
    }

    public void getOne(Context ctx){
        int id = Integer.parseInt(ctx.pathParam("id"));
        Task task = tasks.stream().filter(t -> t.id == id).findFirst().orElse(null);
        ctx.json(task);
    }

    public void create(Context ctx){
        Task task = ctx.bodyAsClass(Task.class);
        tasks.add(task);
        ctx.status(201);
    }

    public void delete(Context ctx) {
        int id = Integer.parseInt(ctx.pathParam("id"));
        tasks.removeIf(task -> task.id == id);
        ctx.status(204);
    }

    public void update(Context ctx) {
        int id = Integer.parseInt(ctx.pathParam("id"));
        Task newTask = ctx.bodyAsClass(Task.class);
        tasks.removeIf(task -> task.id == id);
        tasks.add(newTask);
        ctx.status(200);
    }
}