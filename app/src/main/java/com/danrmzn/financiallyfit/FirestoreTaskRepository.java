package com.danrmzn.financiallyfit;
//
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class FirestoreTaskRepository {
    private final FirebaseFirestore firestore;
    private final FirebaseAuth auth;

    public FirestoreTaskRepository() {
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
    }

    private String getUserId() throws Exception {
        if (auth.getCurrentUser() != null) {
            return auth.getCurrentUser().getUid();
        } else {
            throw new Exception("User not authenticated");
        }
    }

    // Save a Task
    public void saveTask(Task task, SignInCallback callback) {
        try {
            String userId = getUserId();
            CollectionReference taskCollection = firestore.collection("users").document(userId).collection("tasks");
            taskCollection.add(task)
                    .addOnSuccessListener(documentReference -> callback.onSuccess())
                    .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
        } catch (Exception e) {
            callback.onFailure(e.getMessage());
        }
    }

    // Fetch All Tasks
    public void fetchTasks(FirestoreFetchCallback<List<Task>> callback) {
        try {
            String userId = getUserId();
            firestore.collection("users")
                    .document(userId)
                    .collection("tasks")
                    .get()
                    .addOnSuccessListener(querySnapshot -> {
                        List<Task> tasks = querySnapshot.toObjects(Task.class);
                        callback.onSuccess(tasks);
                    })
                    .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
        } catch (Exception e) {
            callback.onFailure(e.getMessage());
        }
    }
}
