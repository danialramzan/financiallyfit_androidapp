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
            System.out.println(tag + "already signed in");
            return true;
        }
        return false;
    }

    public boolean signIn() {
        if (isSignedIn()) {
            return true;
        }

        try {
            buildCredentialRequest();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof InterruptedException) Thread.currentThread().interrupt();

            System.out.println(tag + "signIn error: " + e.getMessage());
            return false;
        }
    }

    private boolean handleSignIn(GetCredentialResponse result) {
        Object credential = result.getCredential();

        if (credential instanceof CustomCredential &&
                ((CustomCredential) credential).getType().equals(GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL)) {

            try {
                GoogleIdTokenCredential tokenCredential = GoogleIdTokenCredential.createFrom(((CustomCredential) credential).getData());

                System.out.println(tag + "name: " + tokenCredential.getDisplayName());
                System.out.println(tag + "email: " + tokenCredential.getId());
                System.out.println(tag + "image: " + tokenCredential.getProfilePictureUri());

                String idToken = tokenCredential.getIdToken();

                AuthCredential authCredential = GoogleAuthProvider.getCredential(idToken, null);

                // Sign in with the AuthCredential


                // val authResult = firebaseAuth.signInWithCredential(authCredential).await()
                FirebaseAuth.getInstance().signInWithCredential(authCredential);

                // Check if the user is signed in
                return firebaseAuth.getCurrentUser() != null;





//                val authCredential = GoogleAuthProvider.getCredential(
//                        tokenCredential.idToken, null
//                )
//                val authResult = firebaseAuth.signInWithCredential(authCredential).await()

//
//                FirebaseAuth.getInstance().signInWithCredential(tokenCredential.getIdToken());
//
//
//                FirebaseAuth.getInstance().signInWithCredential(GoogleAuthProvider.getCredential();
//
////                FirebaseAuth.getInstance().signInWithCredential(GoogleAuthProvider.getCredential(idToken, null))
////                        .get();
//
//                return firebaseAuth.getCurrentUser() != null;

            } catch (Exception e) {
                System.out.println(tag + "GoogleIdTokenParsingException: " + e.getMessage());
                if (e instanceof InterruptedException) Thread.currentThread().interrupt();
                return false;
            }

        } else {
            System.out.println(tag + "credential is not GoogleIdTokenCredential");
            return false;
        }
    }

//    private GetCredentialResponse buildCredentialRequest() throws Exception {
//
//        GetCredentialRequest request = new GetCredentialRequest.Builder()
//                .addCredentialOption(
//                        new GetGoogleIdOption.Builder()
//                                .setFilterByAuthorizedAccounts(false)
//                                .setServerClientId("xxxxx")
//                                .setAutoSelectEnabled(false)
//                                .build()
//                )
//                .build();
//
//
//        return credentialManager.getCredentialAsync(
//                context,
//                request,
//                new CancellationSignal(),
//                Executors.newSingleThreadExecutor(),
//                new CredentialManagerCallback<GetCredentialResponse, GetCredentialException>() {
//                    @Override
//                    public void onResult(GetCredentialResponse result) {
//                        handleSignIn(result);
//                    }
//
//                    @Override
//                    public void onError(GetCredentialException e) {
//                        System.out.println("lol");
//
////                        Log.e("Error!");
////                        handleFailure(e);
//                    }
//                }
//        );
//
////        return credentialManager.getCredential(request, this.context);
//    }

    private void buildCredentialRequest() {
        GetCredentialRequest request = new GetCredentialRequest.Builder()
                .addCredentialOption(
                        new GetGoogleIdOption.Builder()
                                .setFilterByAuthorizedAccounts(false)
                                .setServerClientId(
                                        "xxxx"
//                                        System.getenv("GOOGLE_CLIENT_ID")
                                )
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
                        handleSignIn(result);
                    }

                    @Override
                    public void onError(GetCredentialException e) {
                        // Handle the error here
                        System.err.println("Error fetching credentials: " + e.getMessage());
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
