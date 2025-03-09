package com.danrmzn.financiallyfit;

public interface SignInCallback {
        void onSuccess();

        void onFailure(String errorMessage);
}
