package com.danrmzn.financiallyfit.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Build
import android.util.Log
import android.widget.TimePicker
import android.widget.Toast
import androidx.annotation.RequiresApi
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.danrmzn.financiallyfit.Task
import com.danrmzn.financiallyfit.TaskList
import com.danrmzn.financiallyfit.TaskType
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.json.responseJson
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import okhttp3.OkHttpClient
import okhttp3.Request
import com.github.kittinunf.result.Result
import com.stripe.android.core.networking.StripeRequest
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import org.json.JSONArray
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import network.chaintech.kmp_date_time_picker.ui.datetimepicker.WheelDateTimePickerView
import network.chaintech.kmp_date_time_picker.utils.DateTimePickerView
import network.chaintech.kmp_date_time_picker.utils.MAX
import network.chaintech.kmp_date_time_picker.utils.MIN
import network.chaintech.kmp_date_time_picker.utils.TimeFormat
import network.chaintech.kmp_date_time_picker.utils.now
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskScreen(navController: NavController) {
    var taskName by rememberSaveable { mutableStateOf("") }
    var selectedOption by rememberSaveable { mutableStateOf(TaskType.None.name) }
    var hours by rememberSaveable { mutableStateOf("") }
    var minutes by rememberSaveable { mutableStateOf("") }
    var currency by rememberSaveable { mutableStateOf("") }
    var moneyAmount by rememberSaveable { mutableStateOf("") }
    var taskList by rememberSaveable { mutableStateOf<TaskList?>(null) } // TaskList starts as null
    val repOptions = (1..100).map { it.toString() } // Options for reps (1-100)
    var selectedReps by rememberSaveable { mutableStateOf("") }
    var currencyOptions by rememberSaveable { mutableStateOf(emptyList<String>()) }

    var showTimePicker by rememberSaveable { mutableStateOf(false) }
    var showDatePicker by rememberSaveable { mutableStateOf(false) }

    var dateConfirmed by rememberSaveable { mutableStateOf(false) }
    var timeConfirmed by rememberSaveable { mutableStateOf(false) }

    val state = rememberTimePickerState()
    val formatter = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }
    val snackState = remember { SnackbarHostState() }
    val snackScope = rememberCoroutineScope()


    LaunchedEffect(Unit) {
        val user = FirebaseAuth.getInstance().currentUser

        user?.getIdToken(true)?.addOnSuccessListener { result ->
            val token = result.token
            Log.wtf("Auth", "Firebase Token: $token")

            // Fetch the currency list from the backend
            "https://us-central1-financiallyfit-52b32.cloudfunctions.net/get_supported_currencies"
                .httpGet()
                .header("Authorization", "Bearer $token")
                .responseJson { _, _, result ->
                    if (result is Result.Success) {
                        try {
                            val currencyList: List<String> =
                                Json.decodeFromString(result.get().content)
                            currencyOptions = currencyList
                            Log.wtf("GNX", "Fetched Currencies: $currencyOptions")
                        } catch (e: Exception) {
                            Log.wtf("GNX", "JSON Parsing Error: ${e.message}")
                        }
                    } else {
                        Log.wtf("GNX", "Failed to fetch currencies")
                    }
                }
        }?.addOnFailureListener {
            Log.wtf("AUTH", "Firebase Auth Error: ${it.message}")
        }
    }


    var expandedHours by rememberSaveable { mutableStateOf(false) }
    var expandedMinutes by rememberSaveable { mutableStateOf(false) }
    var expandedCurrencies by rememberSaveable { mutableStateOf(false) }
    var repsDropdownVisible by rememberSaveable { mutableStateOf(false) }
    var isDialogOpen by rememberSaveable { mutableStateOf(false) }
    val calendar = Calendar.getInstance()


    val context = LocalContext.current


    var selectedHour by remember { mutableStateOf(0) }
    var selectedMinute by remember { mutableStateOf(0) }
    var timeString by remember { mutableStateOf("Select Time") }


    // this function crashes, fix it
    LaunchedEffect(Unit) {
        val result = try {
            Log.d("GNX", "before")
            currencyOptions = getCurrencyOptions()  // ✅ Fetch data only once
        } catch (e: Exception) {
            Toast.makeText(
                context,
                e.message.toString()
                //"Failed to load data, please check your internet connection!"
                , Toast.LENGTH_SHORT
            ).show()
            currencyOptions = emptyList<String>()
        }
    }


    val temporaryTaskList = rememberSaveable(
        saver = Saver<SnapshotStateList<Task>, List<Map<String, Any>>>(
            save = { list ->
                list.map { task ->
                    mapOf(
                        "name" to task.getName(),
                        "type" to task.getType().name,
                        "completed" to task.isCompleted(),
                        "reps" to task.getReps()
                    )
                }
            },
            restore = { savedList ->
                SnapshotStateList<Task>().apply {
                    addAll(
                        savedList.map { map ->
                            Task(
                                map["name"] as String,
                                TaskType.valueOf(map["type"] as String),
                                (map["reps"] as Number).toInt()
                            ).apply {
                                setCompletionStatus(map["completed"] as Boolean)
                            }
                        }
                    )
                }
            }
        )
    ) {
        mutableStateListOf<Task>()
    }


    val hourOptions = (0..24).map { it.toString().padStart(2, '0') }
    val minuteOptions = (0..59).map { it.toString().padStart(2, '0') }
    var commit by rememberSaveable { mutableStateOf(false) }

    if (!commit) {

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
                Text(
                    text = "Add tasks using the dropdown and remove them by clicking the cross if needed.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 16.dp) // Space below the tagline
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

                            // Display Each Task Below the Heading with a Remove Button
                            temporaryTaskList.forEachIndexed { index, task ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 4.dp), // Space between tasks
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (task.type == TaskType.valueOf("Reps")) {
                                        Text(
                                            text = buildAnnotatedString {
                                                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                                    append("${task.reps}x ") // Bold reps count
                                                }
                                                append(task.name) // Task name
                                            },
                                            style = MaterialTheme.typography.bodyMedium,
                                            modifier = Modifier.weight(1f) // Take up remaining space
                                        )
                                    } else {
                                        Text(
                                            text = task.name,
                                            style = MaterialTheme.typography.bodyMedium,
                                            modifier = Modifier.weight(1f) // Take up remaining space
                                        )
                                    }

                                    // Add a Remove Button
                                    Icon(
                                        imageVector = Icons.Default.Close, // Use Material Design close icon
                                        contentDescription = "Remove Task",
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clickable {
                                                // Remove the task from the list
                                                temporaryTaskList.removeAt(index)
                                            }
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
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { expanded = !expanded },
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
                            TaskType.entries.forEach { option ->
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
                // TASK TYPE DROPDOWN END>>>>>>>>>>>>>>

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
                                        temporaryTaskList.add(Task(taskName.trim(), taskType, 0))
                                    } else {
                                        temporaryTaskList.add(
                                            Task(
                                                taskName.trim(),
                                                taskType,
                                                selectedReps.toInt()
                                            )
                                        )
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
                                commit = true
                            },
                            modifier = Modifier.weight(1f),
                            enabled = temporaryTaskList.isNotEmpty()
                        ) {
                            Text("Commit")
                        }
                    }
                }
            }


        }
    } else {
        // SHALL BE MNOVED TO NEXT SCREEN

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {

            // TaskList is committed, display its content
            item {
//                Text("Task List Committed!")
                Text(
                    text = "Current Task List:",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(bottom = 8.dp) // Space below heading
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp) // ✅ Fixed height
                        .padding(bottom = 8.dp)
                        .border(1.dp, MaterialTheme.colorScheme.surfaceVariant)
                        .padding(8.dp)
                ) {

                    LazyColumn( // ✅ Makes content scrollable if needed
                        modifier = Modifier.fillMaxSize()
                    ) {

                        items(temporaryTaskList.toList()) { task -> // ✅ Use LazyColumn for performance
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
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                            }
                        }
                    }
                }

// Instructional Text Below Task List
                Text(
                    text = "Click 'Go Back' if you need to edit tasks.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 8.dp) // Space below tagline
                )


//                 Temporary Task List Display
//                        Box(
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .padding(bottom = 8.dp)
//                                .border(1.dp, MaterialTheme.colorScheme.surfaceVariant)
//                                .padding(8.dp)
//                        ) {
//                            Column {
//                                // Heading for the Task List
//                                Text(
//                                    text = "Current Task List:",
//                                    style = MaterialTheme.typography.bodyLarge,
//                                    modifier = Modifier.padding(bottom = 8.dp) // Space below the heading
//                                )
//
//                                // Display Each Task Below the Heading
//                                temporaryTaskList.forEach { task ->
//                                    if (task.type == TaskType.valueOf("Reps")) {
//                                        Text(
//                                            text = buildAnnotatedString {
//                                                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
//                                                    append("${task.reps}x ") // Bold reps count
//                                                }
//                                                append(task.name) // Task name
//                                            },
//                                            style = MaterialTheme.typography.bodyMedium,
//                                            modifier = Modifier.padding(bottom = 4.dp) // Space between tasks
//                                        )
//                                    } else {
//                                        Text(
//                                            text = task.name,
//                                            style = MaterialTheme.typography.bodyMedium,
//                                            modifier = Modifier.padding(bottom = 4.dp) // Space between tasks
//                                        )
//                                    }
//                            }
//
//                        }
//
//                }
//                Text(
//                    text = "Click 'Go Back' if you need to edit tasks.",
//                    style = MaterialTheme.typography.bodySmall,
//                    color = MaterialTheme.colorScheme.onSurfaceVariant,
//                    modifier = Modifier.padding(bottom = 8.dp) // Space below the tagline
//                )


            }
            // Money Input for TaskList


            /////////////////////////////////////////////// add currency

            item {
                // Money Amount Input (Replacing Hours Dropdown)
                OutlinedTextField(
                    value = moneyAmount,
                    onValueChange = { moneyAmount = it },
                    label = { Text("Amount") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                )

                // Minutes Dropdown (Unchanged)
                Box {
                    OutlinedTextField(
                        value = currency.uppercase(),
                        onValueChange = {},
                        label = { Text("Currency") },
                        readOnly = true,
                        interactionSource = remember { MutableInteractionSource() }
                            .also { interactionSource ->
                                LaunchedEffect(interactionSource) {
                                    interactionSource.interactions.collect {
                                        if (it is PressInteraction.Release) {
                                            expandedCurrencies = !expandedCurrencies
                                        }
                                    }
                                }
                            },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expandedCurrencies = true },
                        trailingIcon = {
                            IconButton(onClick = { expandedCurrencies = true }) {
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                            }
                        }
                    )

                    DropdownMenu(
                        expanded = expandedCurrencies,
                        onDismissRequest = { expandedCurrencies = false }
                    ) {
                        currencyOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option.uppercase()) },
                                onClick = {
                                    currency = option
                                    expandedCurrencies = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Enter the amount of money you are willing to put up.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 8.dp) // Space below the tagline
                )
            }


            /// change to native syustem time picker




            // 1: DATEPICKER -> GET CALENDAR AND SET DATE (WITH 7 DAY LIMIT)
            // 2: DATEPICKER CALLBACK -> TIMEPICKER USING PREVIOUS CALENDAR
            // 3: CONVERT TO ZONEDTIMEZONE
            // 4: profit


            ///
            item {

                Box {
                    Button(onClick = { showDatePicker = true }) {
                        Text("Set Due Date & Time")
                    }

                    SnackbarHost(hostState = snackState)
                }

                if (showDatePicker) {

                    val dpd = DatePickerDialog(
                        context,
                        { _, year, month, day ->
                            calendar.set(Calendar.YEAR, year)
                            calendar.set(Calendar.MONTH, month)
                            calendar.set(Calendar.DAY_OF_MONTH, day)

                            dateConfirmed = true
                            showDatePicker = false
                            showTimePicker = true // Trigger time picker after date selection
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                    )

                    dpd.datePicker.minDate = Calendar.getInstance().timeInMillis
                    dpd.datePicker.maxDate = Calendar.getInstance().
                    apply { add(Calendar.DAY_OF_MONTH, 7)}.timeInMillis

                    dpd.setOnCancelListener { showDatePicker = false }
                    dpd.setOnDismissListener { showDatePicker = false }

                    dpd.show()
                }


                if (showTimePicker) {
                    val timePickerDialog = TimePickerDialog(
                        context,
                        { _: TimePicker, hour: Int, minute: Int ->
                            calendar.set(Calendar.HOUR_OF_DAY, hour)
                            calendar.set(Calendar.MINUTE, minute)

                            snackScope.launch {
                                snackState.showSnackbar("Entered time: $calendar.")
                            }
                            Log.wtf("GNX", calendar.toString())

                            showTimePicker = false
                        },
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE),
                        false
                    )

                    timePickerDialog.show()
                }
            }





            item {


                // allegedly good code

//                    if (isDialogOpen) {
//                        Dialog(onDismissRequest = { onDismissRequest() }) {
//                            Box(
//                                modifier = Modifier
//                                    .fillMaxWidth()
//                                    .padding(16.dp)
//                                    .background(
//                                        color = MaterialTheme.colorScheme.background,
//                                        shape = RoundedCornerShape(8.dp)
//                                    )
//                                    .padding(16.dp) // Inner padding
//                            ) {
//                                Column(
//                                    modifier = Modifier
//                                        .verticalScroll(rememberScrollState()) // Enable scrolling for overflow
//                                ) {
//                                    // Title
//                                    Text(
//                                        text = "Confirm Payment and Start Task",
//                                        style = MaterialTheme.typography.headlineSmall,
//                                        modifier = Modifier.padding(bottom = 8.dp)
//                                    )
//
//                                    // Display the total amount of money
//                                    Text(text = "Amount: $$moneyAmount")
//
//                                    Spacer(modifier = Modifier.height(8.dp))
//
//                                    // Calculate start and end times using LocalTime
//                                    val currentTime = LocalDateTime.now()
//                                    val startTime = currentTime.plusMinutes(5)
//                                    val endTime = startTime.plusMinutes(hours.toLong() * 60 + minutes.toLong())
//                                    val formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy h:mm a")
//                                    val formattedStartTime = startTime.format(formatter)
//                                    val formattedEndTime = endTime.format(formatter)
//
//                                    // Show the estimated start time and end time
//                                    Text(text = "Estimated start time: $formattedStartTime")
//                                    Text(text = "Estimated end time: $formattedEndTime")
//
//                                    Spacer(modifier = Modifier.height(8.dp))
//
//                                    // Inform the user about task start and preauthorization
//                                    Text(
//                                        text = "The time to do your task will start as soon as payment is pre-authorized. " +
//                                                "The above times are given for reference only and assume payment is pre-authorized in 5 minutes from now."
//                                    )
//
//                                    Spacer(modifier = Modifier.height(16.dp))
//
//                                    // Display the task list
//                                    Text(text = "Tasks to complete:")
//                                    temporaryTaskList.forEach { task ->
//                                        if (task.type == TaskType.valueOf("Reps")) {
//                                            Text(
//                                                text = buildAnnotatedString {
//                                                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
//                                                        append("${task.reps}x ") // Bold reps count
//                                                    }
//                                                    append(task.name) // Task name
//                                                },
//                                                style = MaterialTheme.typography.bodyMedium,
//                                                modifier = Modifier.padding(bottom = 4.dp)
//                                            )
//                                        } else {
//                                            Text(
//                                                text = task.name,
//                                                style = MaterialTheme.typography.bodyMedium,
//                                                modifier = Modifier.padding(bottom = 4.dp)
//                                            )
//                                        }
//                                    }
//
//                                    Spacer(modifier = Modifier.height(16.dp))
//
//                                    // Buttons Row
//                                    Row(
//                                        horizontalArrangement = Arrangement.End,
//                                        modifier = Modifier.fillMaxWidth()
//                                    ) {
//                                        TextButton(onClick = { onDismissRequest() }) {
//                                            Text("Cancel")
//                                        }
//                                        Spacer(modifier = Modifier.width(8.dp))
//                                        TextButton(onClick = {
//                                            println("Navigating to payment...")
//                                            onDismissRequest()
//                                        }) {
//                                            Text("Confirm")
//                                        }
//                                    }
//                                }
//                            }
//                        }


                if (isDialogOpen) {
                    AlertDialog(
                        onDismissRequest = { isDialogOpen = false },
                        title = {
                            Text(text = "Confirm Payment and Start Task")
                        },
                        text = {
                            Column(
                                modifier = Modifier
                                    .verticalScroll(rememberScrollState()) // Add scrolling capability
                                    .padding(vertical = 8.dp)
//                                        .weight(weight = 1f, fill = false)
                            ) {
                                // Display the total amount of money
                                Text(text = "Amount: $$moneyAmount")

                                Spacer(modifier = Modifier.height(8.dp))


                                // Calculate start and end times using LocalTime
                                val currentTime = LocalDateTime.now()
                                val startTime = currentTime.plusMinutes(5)
                                val endTime =
                                    startTime.plusMinutes(hours.toLong() * 60 + minutes.toLong())
                                val formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy h:mm a")
                                val formattedStartTime = startTime.format(formatter)
                                val formattedEndTime = endTime.format(formatter)

                                // Show the estimated start time and end time
                                Text(text = "Estimated start time: $formattedStartTime")
                                Text(text = "Estimated end time: $formattedEndTime")

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = "The time to do your task will start as soon as payment is pre-authorized. " +
                                            "The above times give are given for reference only and assume payment is pre-authorized in 5 minutes from now."
                                )








                                Spacer(modifier = Modifier.height(8.dp))

                                // Inform the user about task start and preauthorization

                                Spacer(modifier = Modifier.height(16.dp))

                                // Display the task list
//                                    Text(text = "Tasks to complete:")
//                                    temporaryTaskList.forEach { task ->
//                                        if (task.type == TaskType.valueOf("Reps")) {
//                                            Text(
//                                                text = buildAnnotatedString {
//                                                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
//                                                        append("${task.reps}x ") // Bold reps count
//                                                    }
//                                                    append(task.name) // Task name
//                                                },
//                                                style = MaterialTheme.typography.bodyMedium,
//                                                modifier = Modifier.padding(bottom = 4.dp) // Space between tasks
//                                            )
//                                        } else {
//                                            Text(
//                                                text = task.name,
//                                                style = MaterialTheme.typography.bodyMedium,
//                                                modifier = Modifier.padding(bottom = 4.dp) // Space between tasks
//                                            )
//                                        }
//                                    }
                            }
                        },
                        confirmButton = {
                            TextButton(onClick = {
                                // Navigate to payment or perform payment logic

                                // init stripe callback
                                // once done,


                                isDialogOpen = false
                                println("Navigating to payment...")
                                navController.navigate("payment")
                            }) {
                                Text("Confirm and Proceed to Payment")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { isDialogOpen = false }) {
                                Text("Cancel")
                            }
                        }
                    )
                }
            }
        }
    }
}

suspend fun getCurrencyOptions(): List<String> {
    val user = FirebaseAuth.getInstance().currentUser ?: return emptyList()
    val token = user.getIdToken(true).await().token ?: return emptyList()
    Log.d("GNX", "1")
    val client = OkHttpClient()
    val request = Request.Builder()
        .url(
            "https://us-central1-financiallyfit-52b32.cloudfunctions.net/get_supported_currencies"
        )
        .header("Authorization", "Bearer $token")
        .build()
    Log.d("GNX", "2")

    // val response = client.newCall(request).execute()

    try {
        Log.d("GNX", "Executing request...")
        val response = client.newCall(request).execute()
        Log.d("GNX", "3 - Response received")
    } catch (e: Exception) {
        Log.e("GNX", "Request failed: ${e.message}", e)
    }
    return emptyList<String>()


//    if (!response.isSuccessful) throw Exception("Request failed with status code: ${response.code}")
//    val responseText = response.body?.string() ?: throw Exception("Response body is null")
//    val jsonResponse = JSONArray(responseText)
//    return List(jsonResponse.length()) { jsonResponse.getString(it) }
}


fun isValidTaskInput(
    moneyAmount: String,
    hours: String,
    minutes: String
): Boolean {
    return moneyAmount.isNotEmpty() &&
            hours.isNotEmpty() &&
            minutes.isNotEmpty() &&
            ((hours.toIntOrNull() ?: 0) > 0 || (minutes.toIntOrNull() ?: 0) >= 30)
}
