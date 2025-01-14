package com.danrmzn.financiallyfit;

import java.time.LocalDateTime;

public class Task {
    private String name;
    private TaskType type;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public Task(String name, TaskType type) {
        this.name = name;
        this.type = type;
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


    @Override
    public String toString() {
        return name + " - " + type.name() + " - Start: " + startTime + " - End: " + endTime;
    }
}
