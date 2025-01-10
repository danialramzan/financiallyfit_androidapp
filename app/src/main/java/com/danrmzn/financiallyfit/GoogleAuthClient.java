package com.danrmzn.financiallyfit;

import android.content.Context;
import android.os.CancellationSignal;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.credentials.ClearCredentialStateRequest;
import androidx.credentials.CredentialManager;
import androidx.credentials.CredentialManagerCallback;
import androidx.credentials.CustomCredential;
import androidx.credentials.GetCredentialRequest;
import androidx.credentials.GetCredentialResponse;
import androidx.credentials.exceptions.ClearCredentialException;
import androidx.credentials.exceptions.GetCredentialException;

import com.google.android.libraries.identity.googleid.GetGoogleIdOption;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

public class GoogleAuthClient {

    private final String tag = "GoogleAuthClient: ";
    private final CredentialManager credentialManager;
    private final FirebaseAuth firebaseAuth;
    private Context context;

    public GoogleAuthClient(Context context) {
        this.context = context;
        this.credentialManager = CredentialManager.create(context);
        this.firebaseAuth = FirebaseAuth.getInstance();
    }

    public boolean isSignedIn() {
        if (firebaseAuth.getCurrentUser() != null) {
            Log.i("skibidi",tag + "already signed in");
            return true;
        }
        return false;
    }

    public interface SignInCallback {
        void onSuccess();
        void onFailure(String errorMessage);
    }

    public void signIn(SignInCallback callback) {
        if (isSignedIn()) {
            callback.onSuccess();
            return;
        }

        try {
            buildCredentialRequest(callback);
        } catch (Exception e) {
            Log.e("skibidi", "Sign-In error: " + e.getMessage());
            callback.onFailure("Sign-In error: " + e.getMessage());
        }
    }


    // Handle sign-in result


    private void handleSignIn(GetCredentialResponse result, SignInCallback callback) {
        Object credential = result.getCredential();

        if (credential instanceof CustomCredential &&
                ((CustomCredential) credential).getType().equals(GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL)) {

            try {
                GoogleIdTokenCredential tokenCredential = GoogleIdTokenCredential.createFrom(((CustomCredential) credential).getData());

                Log.i("skibidi", tag + "name: " + tokenCredential.getDisplayName());
                Log.i("skibidi", tag + "email: " + tokenCredential.getId());
                Log.i("skibidi", tag + "image: " + tokenCredential.getProfilePictureUri());

                String idToken = tokenCredential.getIdToken();

                AuthCredential authCredential = GoogleAuthProvider.getCredential(idToken, null);

                // Sign in with Firebase Authentication
                firebaseAuth.signInWithCredential(authCredential)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Log.i(tag, "Firebase authentication successful.");
                                callback.onSuccess(); // Notify success
                            } else {
                                Log.e(tag, "Firebase authentication failed: " + task.getException().getMessage());
                                callback.onFailure("Firebase authentication failed: " + task.getException().getMessage());
                            }
                        });

            } catch (Exception e) {
                Log.e("skibidi", tag + "GoogleIdTokenParsingException: " + e.getMessage());
                if (e instanceof InterruptedException) Thread.currentThread().interrupt();
                callback.onFailure("Sign-In error: " + e.getMessage());
            }

        } else {
            Log.e("skibidi", tag + "Credential is not GoogleIdTokenCredential");
            callback.onFailure("Invalid credentials");
        }
    }

    public void buildCredentialRequest(SignInCallback callback) {
        GetCredentialRequest request = new GetCredentialRequest.Builder()
                .addCredentialOption(
                        new GetGoogleIdOption.Builder()
                                .setFilterByAuthorizedAccounts(false)
                                .setServerClientId("815556289078-03297lb6f2d71v5qqqlk7vkvjra120se.apps.googleusercontent.com")
                                .setAutoSelectEnabled(false)
                                .build()
                )
                .build();

        credentialManager.getCredentialAsync(
                context,
                request,
                new CancellationSignal(),
                Executors.newSingleThreadExecutor(),
                new CredentialManagerCallback<GetCredentialResponse, GetCredentialException>() {
                    @Override
                    public void onResult(GetCredentialResponse result) {
                        // Handle the result here (e.g., sign in the user)
                        handleSignIn(result, callback);
                    }

                    @Override
                    public void onError(GetCredentialException e) {
                        // Handle the error here
                        Log.e("skibidi", "Error fetching credentials: " + e.getMessage());
                        callback.onFailure("Error fetching credentials: " + e.getMessage());
                    }
                }
        );
    }

    public void signOut() {

//        ClearCredentialStateRequest request = new ClearCredentialStateRequest();
//
//
//        credentialManager.clearCredentialStateAsync(
//                request,
//                null, // pass in a CancelationSignal to allow cancelling the request
//                Runnable::run, // Execute the callback immediately
//                new CredentialManagerCallback<Void, ClearCredentialException>() {
//                    @Override
//                    public void onResult(@NonNull Void result) {
//                        // Handle success
//                    }
//
//                    @Override
//                    public void onError(@NonNull ClearCredentialException e) {
//                        // Handle errors
//                    }
//                });
        firebaseAuth.signOut();
    }

}
