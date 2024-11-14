package com.example.mytodolist;

public class Task {
    private int id, status;
    private String task, taskDescription;

    public Task(int id, int status, String task, String taskDescription) {
        this.id = id;
        this.status = status;
        this.task = task;
        this.taskDescription = taskDescription;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }
}
