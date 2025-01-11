package com.danrmzn.financiallyfit.ui

import android.annotation.SuppressLint
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


import androidx.compose.foundation.Image
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.selection.toggleable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.rememberAsyncImagePainter

//import coil



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

//@Composable
//fun BottomNavigationBar(navController: NavHostController) {
//    val currentDestination by navController.currentBackStackEntryAsState()
//
//    NavigationBar {
//        NavigationBarItem(
//            selected = currentDestination?.destination?.route == "Home",
//            onClick = {
//                if (currentDestination?.destination?.route != "Home") {
//                    navController.navigate("Home")
//                }
//            },
//            label = { Text("Home") },
//            icon = { Icon(Icons.Default.Home, contentDescription = "Home") }
//        )
//        NavigationBarItem(
//            selected = currentDestination?.destination?.route == "idk",
//            onClick = {
//                if (currentDestination?.destination?.route != "idk") {
//                    navController.navigate("idk")
//                }
//            },
//            label = { Text("idk") },
//            icon = { Icon(Icons.Default.Info, contentDescription = "idk") }
//        )
//        NavigationBarItem(
//            selected = currentDestination?.destination?.route == "Settings",
//            onClick = {
//                if (currentDestination?.destination?.route != "Settings") {
//                    navController.navigate("Settings")
//                }
//            },
//            label = { Text("Settings") },
//            icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") }
//        )
//    }
//}



@SuppressLint("UnrememberedMutableInteractionSource")
@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val currentDestination by navController.currentBackStackEntryAsState()

    // Define reusable colorsremember { M
    val navBarItemColors = NavigationBarItemDefaults.colors(
        selectedIconColor = MaterialTheme.colorScheme.primary,
        selectedTextColor = MaterialTheme.colorScheme.primary,
        indicatorColor = Color.Transparent, // Oval highlight color
        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
    )



    NavigationBar {
        NavigationBarItem(
            selected = currentDestination?.destination?.route == "Home",
            onClick = {
                if (currentDestination?.destination?.route != "Home") {
                    navController.navigate("Home")
                }
            },
            label = { Text("Home") },
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            colors = navBarItemColors,
            modifier = Modifier.padding(horizontal = 8.dp),
            interactionSource = MutableInteractionSource()
        )
        NavigationBarItem(
            selected = currentDestination?.destination?.route == "idk",
            onClick = {
                if (currentDestination?.destination?.route != "idk") {
                    navController.navigate("idk")
                }
            },
            label = { Text("idk") },
            icon = { Icon(Icons.Default.Info, contentDescription = "idk") },
            colors = navBarItemColors,
            modifier = Modifier.padding(horizontal = 8.dp),
            interactionSource = MutableInteractionSource()
        )
        NavigationBarItem(
            selected = currentDestination?.destination?.route == "Settings",
            onClick = {
                if (currentDestination?.destination?.route != "Settings") {
                    navController.navigate("Settings")
                }
            },
            label = { Text("Settings") },
            icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
            colors = navBarItemColors,
            modifier = Modifier.padding(horizontal = 8.dp),
            interactionSource = MutableInteractionSource()
        )
    }
}



@Composable
fun NavigationGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "Home") {
        composable("Home") {
            Screen1()
        }
        composable("idk") {
            Screen2()
        }
        composable("Settings") {
            Screen3()
        }
    }
}

@Composable
fun Screen3() {
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

                Image(
                    painter = rememberAsyncImagePainter(profilePicture),
                   contentDescription = "Sample Image",
                    contentScale = ContentScale.Crop, // Adjust to fit or crop the image
                    modifier = Modifier.fillMaxWidth()
                )


//                Text(text = "Profile picture available at $profilePicture") // Placeholder

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
fun Screen1() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "This is Screen 1")
    }
}
