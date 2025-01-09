package com.danrmzn.financiallyfit;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;

import com.danrmzn.financiallyfit.MainActivity;
import com.danrmzn.financiallyfit.R;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity  {

    private FirebaseAuth mAuth; // Firebase Authentication instance
    GoogleAuthClient googleAuthClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login); // Links the layout to this Activity

        // Initialize Firebase Authentication
        mAuth = FirebaseAuth.getInstance();

        // Check if user is already logged in
        if (mAuth.getCurrentUser() != null) {

//            for (int i = 0; i < 10; i++) {
//                Log.e("tag", "********************************");
//            }
//            Log.e("tag", mAuth.getCurrentUser().toString());
            navigateToMain(); // Go to MainActivity if logged in
        }

        // Get references to input fields and button
        EditText emailEditText = findViewById(R.id.editTextEmail);
        EditText passwordEditText = findViewById(R.id.editTextPassword);
        Button loginButton = findViewById(R.id.buttonLogin);
        Button googleButton = findViewById(R.id.buttonGoogleSignIn);



        // login button action <->

        // Set the login button action
        loginButton.setOnClickListener(view -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Sign in the user using Firebase Authentication
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
//                            navigateToMain(); // Navigate to MainActivity on success
                        } else {
                            Toast.makeText(this, "Authentication failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        // login buton action end <->


        // google sign in button

//        googleButton.setOnClickListener(view -> {
//
//            // Sign in the user using Firebase Authentication
//            mAuth.signInWithEmailAndPassword(email, password)
//                    .addOnCompleteListener(task -> {
//                        if (task.isSuccessful()) {
//                            navigateToMain(); // Navigate to MainActivity on success
//                        } else {
//                            Toast.makeText(this, "Authentication failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                        }
//                    });
//        });

//        googleButton.setOnClickListener(view -> {
//            // Sign in with Google
//            lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START); // Mimic lifecycle for coroutine scope
//            new Thread(() -> {
//                boolean isSignInSuccessful = googleAuthClient.signIn();
//                runOnUiThread(() -> {
//                    if (isSignInSuccessful) {
//                        Toast.makeText(this, "Google Sign-In successful", Toast.LENGTH_SHORT).show();
//                        navigateToMain(); // Navigate to MainActivity
//                    } else {
//                        Toast.makeText(this, "Google Sign-In failed", Toast.LENGTH_SHORT).show();
//                    }
//                });
//            }).start();
//        });

        googleButton.setOnClickListener(view -> {
            // Perform Google Sign-In on a background thread

            googleAuthClient = new GoogleAuthClient(getApplicationContext());



            new Thread(() -> {
                boolean isSignInSuccessful = googleAuthClient.signIn();
                runOnUiThread(() -> {
                    if (isSignInSuccessful) {
                        Toast.makeText(this, "Google Sign-In successful", Toast.LENGTH_SHORT).show();
                        Log.wtf("wtf", "EXECUTED!");
                        navigateToMain(); // Navigate to MainActivity
                    } else {
                        Toast.makeText(this, "Google Sign-In failed", Toast.LENGTH_SHORT).show();
                    }
                });
            }).start();
        });
    }







    // Redirect to MainActivity
    private void navigateToMain() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish(); // Close LoginActivity so the user cannot go back to it
    }
}
