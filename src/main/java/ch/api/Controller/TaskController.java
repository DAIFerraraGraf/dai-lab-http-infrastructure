package ch.api.Controller;

import ch.api.Class.Task;
import io.javalin.http.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class TaskController {
    private ConcurrentHashMap<Integer, Task> tasks = new ConcurrentHashMap<Integer, Task>();
    private int lastId = 0;
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
}
