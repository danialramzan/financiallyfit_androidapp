package com.danrmzn.financiallyfit.ui

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.danrmzn.financiallyfit.GoogleAuthClient
import com.danrmzn.financiallyfit.LoginActivity
import com.google.firebase.auth.FirebaseAuth


// who up singletoning
object CURRENTUSER {
    val NAME: String? = FirebaseAuth.getInstance().currentUser?.displayName ?: "Unknown User"
    val EMAIL: String? = FirebaseAuth.getInstance()
        .currentUser?.email ?: "cannotfetchuser@firebase.com"
    val PROFILE_PICTURE: String? = FirebaseAuth.getInstance()
        .currentUser?.photoUrl?.toString() ?: "www.example.com"
}


@Composable
fun MainScreen() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            NavigationGraph(navController = navController)
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val currentDestination = navController.currentBackStackEntry?.destination?.route

    NavigationBar {
        NavigationBarItem(
            selected = currentDestination == "screen1", // Highlight if on screen1
            onClick = { navController.navigate("screen1") },
            label = { Text("Screen 1") },
            icon = { Icon(Icons.Default.Home, contentDescription = "Screen 1") }
        )
        NavigationBarItem(
            selected = currentDestination == "screen2", // Highlight if on screen2
            onClick = { navController.navigate("screen2") },
            label = { Text("Screen 2") },
            icon = { Icon(Icons.Default.Settings, contentDescription = "Screen 2") }
        )
        NavigationBarItem(
            selected = currentDestination == "screen3", // Highlight if on screen3
            onClick = { navController.navigate("screen3") },
            label = { Text("Screen 3") },
            icon = { Icon(Icons.Default.Info, contentDescription = "Screen 3") }
        )
    }
}

@Composable
fun NavigationGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "screen1") {
        composable("screen1") {
            Screen1()
        }
        composable("screen2") {
            Screen2()
        }
        composable("screen3") {
            Screen3()
        }
    }
}

@Composable
fun Screen1() {
    val context = LocalContext.current
    val googleAuthClient = GoogleAuthClient(context)

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "Welcome, ${CURRENTUSER.NAME}!", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Email: ${CURRENTUSER.EMAIL}", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(16.dp))
            CURRENTUSER.PROFILE_PICTURE?.let { profilePicture ->
                // Use an image loading library like Coil or Glide
                Text(text = "Profile picture available at $profilePicture") // Placeholder
            } ?: Text(text = "No profile picture available")
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = {
                    googleAuthClient.signOut()
                    Toast.makeText(context,
                            "Successfully Logged out!",
                            Toast.LENGTH_SHORT).show()

                    // Optionally navigate to a login screen
                    // For example: navController.navigate("login_screen")
                    val intent = Intent(context, LoginActivity::class.java)

                    // adds the value of flags (bitwise or)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // Clear back stack
                    context.startActivity(intent)

                }
            ) {
                Text("Log Out")
            }
        }

    }
}

@Composable
fun Screen2() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "This is Screen 2")
    }
}

@Composable
fun Screen3() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "This is Screen 3")
    }
}
