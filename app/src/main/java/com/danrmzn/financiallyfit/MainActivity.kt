package com.danrmzn.financiallyfit

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.danrmzn.financiallyfit.ui.MainScreen
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {


        // debug
        FirebaseAuth.getInstance().currentUser?.getIdToken(true)
            ?.addOnSuccessListener { result ->
                result.token?.let { Log.d("GNX", it) } // Copy and paste in Postman
            }


        super.onCreate(savedInstanceState)
        setContent {
            MainScreen() // This renders the entire UI defined by the MainScreen composable
        }
    }
}
