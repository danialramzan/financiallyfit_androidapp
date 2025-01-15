package com.danrmzn.financiallyfit;

public interface FirestoreFetchCallback<T> {
    void onSuccess(T result);
    void onFailure(String errorMessage);
}
