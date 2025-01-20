package com.danrmzn.financiallyfit;

import java.io.Serializable;

public class Task implements Serializable {
    private String name;
    private TaskType type;

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    private boolean completed;
    private int reps;

    private static final long serialVersionUID = 1L;


    public int getReps() {
        return reps;
    }

    public void setReps(int reps) {
        this.reps = reps;
    }

    public Task(String name, TaskType type, Integer reps) {
        this.name = name;
        this.type = type;
        this.completed = false;
        this.reps = reps;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TaskType getType() {
        return type;
    }

    public void setType(TaskType type) {
        this.type = type;
    }

    public void setCompletionStatus(boolean status) {
        this.completed = status;
    }


    @Override
    public String toString() {
        return name + " - " + type.name();
    }
}
