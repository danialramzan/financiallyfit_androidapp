package com.danrmzn.financiallyfit.ui


import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.danrmzn.financiallyfit.PaymentScreen
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


@SuppressLint("UnrememberedMutableInteractionSource")
@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val currentDestination by navController.currentBackStackEntryAsState()
    
    val navBarItemColors = NavigationBarItemDefaults.colors(
        selectedIconColor = MaterialTheme.colorScheme.primary,
        selectedTextColor = MaterialTheme.colorScheme.primary,
        indicatorColor = Color.Transparent, // Oval highlight color
        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
    )

    NavigationBar {
        NavigationBarItem(
            selected = currentDestination?.destination?.route == "New",
            onClick = {
                if (currentDestination?.destination?.route != "New") {
                    navController.navigate("New")
                }
            },
            label = { Text("New") },
            icon = { Icon(Icons.Default.AddCircle, contentDescription = "New") },
            colors = navBarItemColors,
            modifier = Modifier.padding(horizontal = 8.dp),
            interactionSource = MutableInteractionSource()
        )
        NavigationBarItem(
            selected = currentDestination?.destination?.route == "History",
            onClick = {
                if (currentDestination?.destination?.route != "History") {
                    navController.navigate("History")
                }
            },
            label = { Text("History") },
            icon = { Icon(Icons.Default.CalendarMonth, contentDescription = "History") },
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
    NavHost(navController = navController, startDestination = "New") {

        // outline routes here!

//        composable("SCREEN_NAME") {
//            SCREEN_FUNCTION(navController)
//        }

        composable("New") {
            HomeScreen(navController)
        }
        composable("History") {
            HistoryScreen()
        }
        composable("Settings") {
            SettingsScreen()
        }
        composable("addTaskScreen") {
            AddTaskScreen(navController)
        }

        composable("payment") {
            PaymentScreen()
        }
    }
}


@Composable
fun SettingsItem(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit = {}
) {

    val interactionSource = remember { MutableInteractionSource() }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = interactionSource,
                indication = ripple()
            ) {
                onClick()
            }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier
                .size(40.dp)
                .padding(end = 16.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Text(
            text = text,
            style = MaterialTheme.typography.titleLarge
        )
    }
}


@Composable
fun HomeScreen(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopStart),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Welcome, ${CURRENTUSER.NAME}!",
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Ready to commit to a task?",
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Button(
            onClick = {
                // on click logic
                navController.navigate("addTaskScreen")

            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
        ) {
            Text("Let's Go!")
        }
    }
}


