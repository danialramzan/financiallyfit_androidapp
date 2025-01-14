package com.danrmzn.financiallyfit;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TaskList {
    private double moneyAmount;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private final List<Task> tasks;

    public TaskList(double moneyAmount, LocalDateTime startTime, LocalDateTime endTime) {
        this.moneyAmount = moneyAmount;
        this.startTime = startTime;
        this.endTime = endTime;
        this.tasks = new ArrayList<>();
    }

    public void setMoneyAmount(double moneyAmount) {
        this.moneyAmount = moneyAmount;
    }

    public double getMoneyAmount() {
        return moneyAmount;
    }

    public void addTask(Task task) {
        tasks.add(task);
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void clearTasks() {
        tasks.clear();
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }


    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("Money Amount: $" + moneyAmount + "\nTasks:\n");
        for (Task task : tasks) {
            builder.append(task.toString()).append("\n");
        }
        return builder.toString();
    }
}
