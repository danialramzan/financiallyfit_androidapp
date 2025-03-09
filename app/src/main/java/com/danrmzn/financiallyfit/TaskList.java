package com.danrmzn.financiallyfit;


import android.os.Build;
import android.os.CountDownTimer;

import androidx.annotation.RequiresApi;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TaskList {
    private double moneyAmount;
    private String currency;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private final List<Task> tasks;
    private CountDownTimer countDownTimer;
    private boolean active;
    private Calendar calendar;


    public Calendar getCalendar() {
        return calendar;
    }

    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }

    public TaskList() {
        this.tasks = new ArrayList<>();
//        this.moneyAmount = 0;
//        this.currency = "xxx";
//        this.startTime = new LocalDa;
//        this.endTime = startTime.plusHours(hours).plusMinutes(minutes);
//        this.calendar = cal;

        this.countDownTimer =
                new CountDownTimer(100000,1000) {
                    @Override
                    public void onTick(long l) {

                    }

                    @Override
                    public void onFinish() {

                    }
                };
                // GetCountdownTimer.createTimer(startTime, minutes, hours);
        this.active = true;
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

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public void copyTasks(List<Task> newTasks) {
        this.tasks.clear();
        this.tasks.addAll(newTasks);
    }




    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("Money Amount: " + moneyAmount + currency + "\nTasks:\n");
        for (Task task : tasks) {
            builder.append(task.toString()).append("\n");
        }
        builder.append("Due Date: " + calendar);
        return builder.toString();
    }
}
