package com.danrmzn.financiallyfit;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity  {

    private FirebaseAuth mAuth; // Firebase Authentication instance
    GoogleAuthClient googleAuthClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login); // Links the layout to this Activity

        // init firebase
        mAuth = FirebaseAuth.getInstance();

        // Check if user is already logged in
        if (mAuth.getCurrentUser() != null) {
            navigateToMain();
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


        googleButton.setOnClickListener(view -> {
            googleAuthClient = new GoogleAuthClient(
//                  getApplicationContext()
                    this
            );

            googleAuthClient.signIn(new SignInCallback() {
                @Override
                public void onSuccess() {
                    runOnUiThread(() -> {
                        Toast.makeText(LoginActivity.this, "Google Sign-In successful", Toast.LENGTH_SHORT).show();
                        Log.d("GoogleAuth", "Sign-In successful.");
                        navigateToMain(); // Navigate to MainActivity
                    });
                }

                @Override
                public void onFailure(String errorMessage) {
                    runOnUiThread(() -> {
                        Toast.makeText(LoginActivity.this, "Google Sign-In failed: " + errorMessage, Toast.LENGTH_SHORT).show();
                        Log.e("GoogleAuth", "Sign-In failed: " + errorMessage);
                    });
                }
            });
        });

    }

    // Redirect to MainActivity
    private void navigateToMain() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish(); // Close LoginActivity so the user cannot go back to it
    }

}
