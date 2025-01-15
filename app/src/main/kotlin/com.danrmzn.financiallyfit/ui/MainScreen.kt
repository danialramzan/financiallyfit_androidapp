package com.danrmzn.financiallyfit.ui


import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.danrmzn.financiallyfit.GoogleAuthClient
import com.danrmzn.financiallyfit.LoginActivity
import com.danrmzn.financiallyfit.Task
import com.danrmzn.financiallyfit.TaskList
import com.danrmzn.financiallyfit.TaskType
import com.google.firebase.auth.FirebaseAuth
import java.time.LocalDateTime



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
        composable("New") {
            Screen1(navController)
        }
        composable("History") {
            Screen2()
        }
        composable("Settings") {
//            Screen3()
            SettingsScreen()
        }
        composable("addTaskScreen") { // Define the addTaskScreen route
            AddTaskScreen(navController)
        }
    }
}


@Composable
fun SettingsScreen() {
    val context = LocalContext.current
    val googleAuthClient = GoogleAuthClient(context)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header Section
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Profile Picture
                Image(
                    painter = rememberAsyncImagePainter(CURRENTUSER.PROFILE_PICTURE),
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(16.dp))

                // User Info
                Column {
                    Text(
                        text = CURRENTUSER.NAME ?: "User Name",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Text(
                        text = CURRENTUSER.EMAIL ?: "Email Address",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

//        // Divider
        HorizontalDivider(thickness = 1.dp)

        // List of OptionsExitToApp
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 8.dp)
        ) {
            // Add reusable items





//            item { SettingsItem(icon = Icons.Default.Person, text = "Account") }
            item {
                SettingsItem(
                    icon = Icons.Filled.Language,
                    text = "Visit my Website!",
                    onClick = {
                        val intent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://danialramzan.github.io/")
                        )
                        context.startActivity(intent)
                    }
                )
            }
            item {
                SettingsItem(
                    icon = Icons.Default.Description,
                    text = "Terms of Service",
                    onClick = {
                        val intent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://danialramzan.github.io/FinanciallyFit/#terms-of-service")
                        )
                        context.startActivity(intent)
                    }
                )
            }
            item {
                SettingsItem(
                    icon = Icons.Default.Lock,
                    text = "Privacy Policy",
                    onClick = {
                        val intent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://danialramzan.github.io/FinanciallyFit/#privacy-policy")
                        )
                        context.startActivity(intent)
                    }
                )
            }

//            item { SettingsItem(icon = Icons.Default.Favorite, text = "Storage and Data") }
            item {
                SettingsItem(
                    icon = Icons.AutoMirrored.Filled.ExitToApp,
                    text = "Log Out",
                    onClick = {
                        googleAuthClient.signOut()
                        Toast.makeText(context, "Successfully Logged Out!", Toast.LENGTH_SHORT).show()

                        // Optionally navigate to a login screen
                        val intent = Intent(context, LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        context.startActivity(intent)
                    }
                )
            }
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
                indication = ripple() // Adds ripple effect
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

        // Text
        Text(
            text = text,
            style = MaterialTheme.typography.titleLarge
        )
    }
}


@Composable
fun Screen2() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp)
    ) {
        // Content at the top of the screen
        Column(
            modifier = Modifier
                .fillMaxWidth() // Fill only the width, not the entire screen height
                .align(Alignment.TopStart), // Align column to the top-start
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "History",
                style = MaterialTheme.typography.headlineLarge
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}


@Composable
fun Screen1(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp)
    ) {
        // Content at the top of the screen
        Column(
            modifier = Modifier
                .fillMaxWidth() // Fill only the width, not the entire screen height
                .align(Alignment.TopStart), // Align column to the top-start
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

        // Button at the bottom center of the screen
        Button(
            onClick = {
                // on click logic
                navController.navigate("addTaskScreen")

            },
            modifier = Modifier
                .align(Alignment.BottomCenter) // Align button to the bottom center
                .padding(bottom = 16.dp) // Add padding to avoid flush with the edge
        ) {
            Text("Let's Go!")
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AddTaskScreen(navController: NavController) {
    var taskName by remember { mutableStateOf("") }
    var selectedOption by remember { mutableStateOf(TaskType.None.name) }
    var hours by remember { mutableStateOf("") }
    var minutes by remember { mutableStateOf("") }
    var moneyAmount by remember { mutableStateOf("") }
    var taskList by remember { mutableStateOf<TaskList?>(null) } // TaskList starts as null
    val repOptions = (1..100).map { it.toString() } // Options for reps (1-100)
    var selectedReps by remember { mutableStateOf("") }


    var expandedHours by remember { mutableStateOf(false) }
    var expandedMinutes by remember { mutableStateOf(false) }
    var repsDropdownVisible by remember { mutableStateOf(false) }
    val temporaryTaskList = remember { mutableStateListOf<Task>() }
    val hourOptions = (0..24).map { it.toString().padStart(2, '0') }
    val minuteOptions = (0..59).map { it.toString().padStart(2, '0') }


    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        item {
            Text(
                text = "Add Tasks",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        // Temporary Task List Display
        if (temporaryTaskList.isNotEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                        .border(1.dp, MaterialTheme.colorScheme.surfaceVariant)
                        .padding(8.dp)
                ) {
                    Column {
                        // Heading for the Task List
                        Text(
                            text = "Task List:",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(bottom = 8.dp) // Space below the heading
                        )

                        // Display Each Task Below the Heading
                        temporaryTaskList.forEach { task ->
                            if (task.type == TaskType.valueOf("Reps")) {
                                Text(
                                    text = buildAnnotatedString {
                                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                            append("${task.reps}x ") // Bold reps count
                                        }
                                        append(task.name) // Task name
                                    },
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(bottom = 4.dp) // Space between tasks
                                )
                            } else {
                                Text(
                                    text = task.name,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(bottom = 4.dp) // Space between tasks
                                )
                            }
                        }
                    }
                }
            }
        }


        if (taskList == null) {
            // Inputs for adding tasks and setting money

            // TASK TYPE DROPDOWN>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
            item {
                var expanded by remember { mutableStateOf(false) }
                Box {
                    OutlinedTextField(
                        value = selectedOption,
                        onValueChange = {},
                        label = { Text("Task Type") },
                        readOnly = true,
                        interactionSource = remember { MutableInteractionSource() }
                            .also { interactionSource ->
                                LaunchedEffect(interactionSource) {
                                    interactionSource.interactions.collect {
                                        if (it is PressInteraction.Release) {
                                            // works like onClick
                                            expanded = !expanded

                                        }
                                    }
                                }
                            },
                                modifier = Modifier.fillMaxWidth().clickable { expanded = !expanded },
                        trailingIcon = {
                            IconButton(onClick = { expanded = !expanded }) {
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                            }
                        }
                    )
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        TaskType.values().forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option.name) },
                                onClick = {
                                    selectedOption = option.name
                                    expanded = false
                                    repsDropdownVisible = option.name == "Reps"
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
            // TASK TYPE DROPDOWN END>>>>>>>>>>>>>>>

            if (repsDropdownVisible) {
                item {
                    var repsExpanded by remember { mutableStateOf(false) }

                    Box {
                        OutlinedTextField(
                            value = selectedReps,
                            onValueChange = {},
                            label = { Text("Number of Reps") },
                            readOnly = true,
                            interactionSource = remember { MutableInteractionSource() }
                                .also { interactionSource ->
                                    LaunchedEffect(interactionSource) {
                                        interactionSource.interactions.collect {
                                            if (it is PressInteraction.Release) {
                                                repsExpanded = !repsExpanded
                                            }
                                        }
                                    }
                                },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { repsExpanded = !repsExpanded },
                            trailingIcon = {
                                IconButton(onClick = { repsExpanded = !repsExpanded }) {
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                }
                            }
                        )
                        DropdownMenu(
                            expanded = repsExpanded,
                            onDismissRequest = { repsExpanded = false }
                        ) {
                            repOptions.forEach { reps ->
                                DropdownMenuItem(
                                    text = { Text(reps) },
                                    onClick = {
                                        selectedReps = reps
                                        repsExpanded = false
                                    }
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }


            // Task Name Input
            item {
                OutlinedTextField(
                    value = taskName,
                    onValueChange = { taskName = it },
                    label = { Text("Task Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
            }



            // SHALL BE MNOVED TO NEXT SCREEN

//            item {
//                // Explanation for Hours and Minutes
//                Text(
//                    text = "Set how much time you are willing to give yourself to complete the task.",
//                    style = MaterialTheme.typography.bodyMedium,
//                    modifier = Modifier.padding(bottom = 8.dp, top = 8.dp)
//                )
//            }

//            item {
//                Row(
//                    modifier = Modifier
//                        .fillMaxWidth(),
//                    horizontalArrangement = Arrangement.spacedBy(8.dp)
//                ) {
//                    // Hours Dropdown
//                    Box(modifier = Modifier.weight(1f)) {
//                        OutlinedTextField(
//                            value = hours,
//                            onValueChange = {},
//                            label = { Text("HH") },
//                            readOnly = true,
//                            interactionSource = remember { MutableInteractionSource() }
//                                .also { interactionSource ->
//                                    LaunchedEffect(interactionSource) {
//                                        interactionSource.interactions.collect {
//                                            if (it is PressInteraction.Release) {
//                                                expandedHours = !expandedHours
//                                            }
//                                        }
//                                    }
//                                },
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .clickable { expandedHours = true },
//                            trailingIcon = {
//                                IconButton(onClick = { expandedHours = true }) {
//                                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
//                                }
//                            }
//                        )
//                        DropdownMenu(
//                            expanded = expandedHours,
//                            onDismissRequest = { expandedHours = false }
//                        ) {
//                            hourOptions.forEach { option ->
//                                DropdownMenuItem(
//                                    text = { Text(option) },
//                                    onClick = {
//                                        hours = option
//                                        expandedHours = false
//                                    }
//                                )
//                            }
//                        }
//                    }
//
//                    // Colon Separator
//                    Text(":", modifier = Modifier.align(Alignment.CenterVertically))
//
//                    // Minutes Dropdown
//                    Box(modifier = Modifier.weight(1f)) {
//                        OutlinedTextField(
//                            value = minutes,
//                            onValueChange = {},
//                            label = { Text("MM") },
//                            readOnly = true,
//                            interactionSource = remember { MutableInteractionSource() }
//                                .also { interactionSource ->
//                                    LaunchedEffect(interactionSource) {
//                                        interactionSource.interactions.collect {
//                                            if (it is PressInteraction.Release) {
//                                                expandedMinutes = !expandedMinutes
//                                            }
//                                        }
//                                    }
//                                },
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .clickable { expandedMinutes = true },
//                            trailingIcon = {
//                                IconButton(onClick = { expandedMinutes = true }) {
//                                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
//                                }
//                            }
//                        )
//                        DropdownMenu(
//                            expanded = expandedMinutes,
//                            onDismissRequest = { expandedMinutes = false }
//                        ) {
//                            minuteOptions.forEach { option ->
//                                DropdownMenuItem(
//                                    text = { Text(option) },
//                                    onClick = {
//                                        minutes = option
//                                        expandedMinutes = false
//                                    }
//                                )
//                            }
//                        }
//                    }
//                }
//            }




//
//            // Add Task Button
//            item {
//                Box(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(vertical = 16.dp), // Adjust padding as needed
//                    contentAlignment = Alignment.Center
//                ) {
//                    Column(
//                        horizontalAlignment = Alignment.CenterHorizontally
//                    ) {
//                        Button(onClick = {
//                            if (taskName.isNotBlank()) {
//                                println("Task Added: $taskName, $selectedOption")
//                            }
//                        }) {
//                            Text("Add Task")
//                        }
//                        Spacer(modifier = Modifier.height(20.dp))
//                    }
//                }
//            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Add Task Button
                    Button(
                        onClick = {
                            if (taskName.isNotBlank() && selectedOption.isNotBlank()) {
                                val taskType = TaskType.valueOf(selectedOption)
                                if (selectedOption == "None") {
                                    temporaryTaskList.add(Task(taskName, taskType, 0))
                                } else {
                                    temporaryTaskList.add(Task(taskName, taskType, selectedReps.toInt()))
                                }
                                taskName = ""
                                selectedOption = ""
                                repsDropdownVisible = false
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Add Task")
                    }

                    Spacer(modifier = Modifier.width(20.dp))

                    // Commit Button
                    Button(
                        onClick = {
                            println("Tasks committed: $temporaryTaskList")
                            temporaryTaskList.clear()
                        },
                        modifier = Modifier.weight(1f),
                        enabled = temporaryTaskList.isNotEmpty()
                    ) {
                        Text("Commit")
                    }
                }
            }


//            // Money Input for TaskList
//            item {
//                OutlinedTextField(
//                    value = moneyAmount,
//                    onValueChange = { moneyAmount = it },
//                    label = { Text("Total Amount (Money)") },
//                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
//                    modifier = Modifier.fillMaxWidth()
//                )
//                Spacer(modifier = Modifier.height(16.dp))
//            }
//
//            // Commit Button
//            item {
//                Button(onClick = {
//                    val amount = moneyAmount.toDoubleOrNull() ?: 0.0
//                    taskList = TaskList(amount,
//                        LocalDateTime.now(),
//                        10,10).apply {
////                        setMoneyAmount(amount)
//                        // Populate tasks if needed
//                    }
//                }) {
//                    Text("Commit")
//                }
//                Spacer(modifier = Modifier.height(16.dp))
//            }
        } else {
            // TaskList is committed, display its content
            item {
                Text("Task List Committed!")
                Text(taskList.toString())
            }

            // Option to navigate back or perform further actions
            item {
                Button(onClick = {
                    navController.popBackStack() // Navigate back
                }) {
                    Text("Go Back")
                }
            }
        }
    }
}


