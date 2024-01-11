package ch.api.Controller;

import ch.api.Class.Task;
import io.javalin.http.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class TaskController {
    private ConcurrentHashMap<Integer, Task> tasks = new ConcurrentHashMap<Integer, Task>();
    private int lastId = 0;
    public TaskController(){
        tasks.put(++lastId, new Task("DAI", "reviser le TE"));
        tasks.put(++lastId, new Task("POO", "Faire les devoirs pour le 11.10"));
        tasks.put(++lastId, new Task("PST", "finir le projet"));
        tasks.put(++lastId, new Task("Math3", "avancer sur les exercices"));

    }
    public void getAll(Context ctx){
        ctx.json(tasks);
    }
    public void getOne(Context ctx){
        int id = Integer.parseInt(ctx.pathParam("id"));
        ctx.json(tasks.get(id));
    }
    public void create(Context ctx){
        Task user = ctx.bodyAsClass(Task.class);
        tasks.put(++lastId, user);
        ctx.status(201);

    }
    public void delete(Context ctx) {
        int id = Integer.parseInt(ctx.pathParam("id"));
        tasks.remove(id);
        ctx.status(204);
    }
    public void update(Context ctx) {
        int id = Integer.parseInt(ctx.pathParam("id"));
        Task task = ctx.bodyAsClass(Task.class);
        tasks.put(id, task);
        ctx.status(200);
    }

    public void cookie(Context ctx){
        System.out.println("cookie");
        ctx.cookie("name", "value");
        ctx.status(200);
    }
}
