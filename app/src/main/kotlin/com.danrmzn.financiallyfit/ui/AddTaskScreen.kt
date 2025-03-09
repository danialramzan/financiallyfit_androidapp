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
import com.danrmzn.financiallyfit.PaymentScreen
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

    val snackState = remember { SnackbarHostState() }
    val snackScope = rememberCoroutineScope()
    var finalTaskList by remember { mutableStateOf<TaskList?>(null) }



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


    var expandedCurrencies by rememberSaveable { mutableStateOf(false) }
    var repsDropdownVisible by rememberSaveable { mutableStateOf(false) }
    var triggerPayment by remember { mutableStateOf(false) }
    lateinit var calendar: Calendar
    var selectedTimeFormatted by rememberSaveable { mutableStateOf("") }
    val formatting = SimpleDateFormat("yyyy-MM-dd 'at' hh:mm a", Locale.getDefault())




    val context = LocalContext.current


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

    var commit by rememberSaveable { mutableStateOf(false) }

    PaymentScreen(finalTaskList = finalTaskList, triggerPayment = triggerPayment, onPaymentComplete = { success ->
        if (success) {
            Log.d("GNX", "Payment successful!")
        }
    })

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
                                        taskName = ""
                                        selectedOption = ""
                                        repsDropdownVisible = false
                                    }
                                    if (selectedOption == "Reps" && (selectedReps != ""))  {
                                        temporaryTaskList.add(
                                            Task(
                                                taskName.trim(),
                                                taskType,
                                                selectedReps.toInt()
                                            )
                                        )
                                        taskName = ""
                                        selectedOption = ""
                                        repsDropdownVisible = false
                                    }

                                }
                            },
                            modifier = Modifier.weight(1f),
                        ) {
                            Text("Add Task")
                        }

                        Spacer(modifier = Modifier.width(20.dp))

                        // Commit Button
                        Button(
                            onClick = {
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
        // SHALL BE MOVED TO NEXT SCREEN

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

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    contentAlignment = Alignment.Center // ✅ Ensures button is always centered
                ) {
                    Button(
                        onClick = { showDatePicker = true },
                        modifier = Modifier.align(Alignment.Center) // ✅ Apply alignment to Button
                    ) {
                        if (timeConfirmed) {
                            Text("Set Due Date & Time [SELECTED: $selectedTimeFormatted]")
                        } else {
                            Text("Set Due Date & Time")
                        }
                        }

                    SnackbarHost(hostState = snackState)
                }

                if (showDatePicker) {
                    calendar = Calendar.getInstance()

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

                    dpd.datePicker.minDate = Calendar.getInstance().
                    apply { add(Calendar.MINUTE, 60)}.timeInMillis
                    dpd.datePicker.maxDate = Calendar.getInstance().
                    apply { add(Calendar.DAY_OF_MONTH, 7)}.timeInMillis

                    dpd.setOnCancelListener { showDatePicker = false }
                    dpd.setOnDismissListener { showDatePicker = false }

                    dpd.show()
                }


                if (showTimePicker) {


                    // make new calandar that contains min time.

                    // on hitting confirm button, check if selected time > min time.
                    // if yes, proceed as normal.
                    // if no, do not close the box, rather output toast message outlining minimum
                    // due time
                    val calendar2 = Calendar.getInstance()
                    calendar2.add(Calendar.MINUTE, 60)

                    val tpd = TimePickerDialog(
                        context,
                        { _: TimePicker, hour: Int, minute: Int ->


                            val calendar3 = calendar.clone() as Calendar

                            calendar3.set(Calendar.HOUR_OF_DAY, hour)
                            calendar3.set(Calendar.MINUTE, minute)



                            // 🚀 Check if selected time is before `calendar2`
                            if (calendar3.timeInMillis < calendar2.timeInMillis) {
                                val minTimeFormatted = formatting.format(calendar2.time)


                                snackScope.launch {
                                    snackState.showSnackbar(
                                        "⚠️ Please select a time at least 1 hour from now.\n⏳ Minimum allowed: $minTimeFormatted")
                                }
                            } else {
                                calendar.set(Calendar.HOUR_OF_DAY, hour)
                                calendar.set(Calendar.MINUTE, minute)

                                snackScope.launch {
                                    selectedTimeFormatted = formatting.format(calendar.time)
                                    snackState.showSnackbar("\uD83D\uDCC5 Entered due date: $selectedTimeFormatted")


                                }
                                showTimePicker = false
                                timeConfirmed = true
                                dateConfirmed = true

                            }

                        },
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE),
                        false // 🚀 Change to true for 24-hour format
                    )

                    tpd.setOnCancelListener { showTimePicker = false }
                    tpd.setOnDismissListener { showTimePicker = false }

                    tpd.show()
                }
            }


            item {

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 16.dp) // Add bottom padding for spacing
                ) {
                    Button(
                        onClick = {
                            Log.wtf("DNX", "BEFORE")

                            if (finalTaskList == null) {
                                finalTaskList = TaskList() // ✅ Ensure it’s initialized
                            }

                            finalTaskList?.let { taskList ->
                                taskList.copyTasks(temporaryTaskList.toList())
                                taskList.moneyAmount = moneyAmount.toDoubleOrNull()!!
                                taskList.currency = currency.lowercase()
                                taskList.calendar = calendar
                            }
                            Log.wtf("DNX", finalTaskList.toString())
                            triggerPayment = true
//                            navController.navigate("payment")
                        },
                        modifier = Modifier
                            .align(Alignment.BottomCenter) // 🚀 Centers at the bottom
                            .padding(horizontal = 16.dp), // Optional: Adds side padding
                        enabled = currency.isNotEmpty()
                                && moneyAmount.isNotEmpty()
                                && timeConfirmed
                                && dateConfirmed
                    ) {
                        Text("Confirm and Proceed to Payment")
                    }
                }




            }
        }
    }
}


