package com.danrmzn.financiallyfit;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class TaskViewModel extends ViewModel {
    private final FirestoreTaskRepository repository;
    private final MutableLiveData<List<Task>> tasks;
    private final MutableLiveData<String> errorMessage;

    public TaskViewModel() {
        repository = new FirestoreTaskRepository();
        tasks = new MutableLiveData<>();
        errorMessage = new MutableLiveData<>();
    }

    public MutableLiveData<List<Task>> getTasks() {
        return tasks;
    }

    public MutableLiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void fetchTasks() {
        repository.fetchTasks(new FirestoreFetchCallback<List<Task>>() {
            @Override
            public void onSuccess(List<Task> result) {
                tasks.setValue(result);
            }

            @Override
            public void onFailure(String errorMessage) {
                TaskViewModel.this.errorMessage.setValue(errorMessage);
            }
        });
    }

    public void saveTask(Task task) {
        repository.saveTask(task, new SignInCallback() {
            @Override
            public void onSuccess() {
                fetchTasks(); // Refresh tasks after saving
            }

            @Override
            public void onFailure(String errorMessage) {
                TaskViewModel.this.errorMessage.setValue(errorMessage);
            }
        });
    }
}
