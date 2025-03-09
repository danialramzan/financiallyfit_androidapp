package com.danrmzn.financiallyfit.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.util.Log
import android.widget.TimePicker
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
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

import com.github.kittinunf.result.Result
import kotlinx.coroutines.launch

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


@Composable
fun AddTaskScreen(navController: NavController) {
    var taskName by rememberSaveable { mutableStateOf("") }
    var selectedOption by rememberSaveable { mutableStateOf(TaskType.None.name) }
    var currency by rememberSaveable { mutableStateOf("") }
    var moneyAmount by rememberSaveable { mutableStateOf("") }
    var taskList by rememberSaveable { mutableStateOf<TaskList?>(null) }
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

    PaymentScreen(
        finalTaskList = finalTaskList,
        triggerPayment = triggerPayment,
        onPaymentComplete = { success ->
            if (success) {
                Log.d("GNX", "Payment successful!")
            }
        })

    // get supported currencies from stripe api with secret key
    LaunchedEffect(Unit) {
        val user = FirebaseAuth.getInstance().currentUser

        user?.getIdToken(true)?.addOnSuccessListener { result ->
            val token = result.token
            Log.wtf("Auth", "Firebase Token: $token")

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


            // first screen task preview
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
                            Text(
                                text = "Task List:",
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            // display all tasks in temporaryTaskList, with a remove button next to them
                            temporaryTaskList.forEachIndexed { index, task ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (task.type == TaskType.valueOf("Reps")) {
                                        Text(
                                            text = buildAnnotatedString {
                                                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                                    append("${task.reps}x ")
                                                }
                                                append(task.name)
                                            },
                                            style = MaterialTheme.typography.bodyMedium,
                                            modifier = Modifier.weight(1f)
                                        )
                                    } else {
                                        Text(
                                            text = task.name,
                                            style = MaterialTheme.typography.bodyMedium,
                                            modifier = Modifier.weight(1f)
                                        )
                                    }

                                    // add remove button in the same row as the task
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Remove Task",
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clickable {
                                                // if removed
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

                // task code implemented
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
                                                // made to ensure dropdown isn't only expanded upon
                                                // only the dropdown arrow but the whole box
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


                // display reps dropdown
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


                // enter task name:
                item {
                    OutlinedTextField(
                        value = taskName,
                        onValueChange = { taskName = it },
                        label = { Text("Task Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // add task and confirm buttons, aligned to be in one row, i am considering
                // repositioning these
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
                                    if (selectedOption == "Reps" && (selectedReps != "")) {
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

                        Spacer(modifier = Modifier.width(10.dp))

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

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {

            // tasklist has been committed.
            item {
                Text(
                    text = "Current Task List:",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(bottom = 8.dp) // Space below heading
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .padding(bottom = 8.dp)
                        .border(1.dp, MaterialTheme.colorScheme.surfaceVariant)
                        .padding(8.dp)
                ) {

                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        // room for refactoring
                        items(temporaryTaskList.toList()) { task ->
                            if (task.type == TaskType.valueOf("Reps")) {
                                Text(
                                    text = buildAnnotatedString {
                                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                            append("${task.reps}x ")
                                        }
                                        append(task.name) // Task name
                                    },
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(bottom = 4.dp)
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

                // instructional text below small text.
                Text(
                    text = "Click 'Go Back' if you need to edit tasks.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 8.dp) // Space below tagline
                )


            }

            // implement money and currency amount buttons below.

            item {

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
                    contentAlignment = Alignment.Center
                ) {
                    Button(
                        onClick = { showDatePicker = true },
                        modifier = Modifier.align(Alignment.Center)
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
                            showTimePicker = true // not really a callback
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                    )

                    dpd.datePicker.minDate =
                        Calendar.getInstance().apply { add(Calendar.MINUTE, 60) }.timeInMillis
                    dpd.datePicker.maxDate =
                        Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, 7) }.timeInMillis

                    dpd.setOnCancelListener { showDatePicker = false }
                    dpd.setOnDismissListener { showDatePicker = false }

                    dpd.show()
                }


                if (showTimePicker) {


                    // TODO: make new calendar that contains min time.

                    // on hitting confirm button, check if selected time > min time.
                    // if yes, proceed as normal.
                    // if not, output toast message outlining minimum due time


                    // calendar2 contains min time.
                    val calendar2 = Calendar.getInstance()
                    calendar2.add(Calendar.MINUTE, 60)

                    val tpd = TimePickerDialog(
                        context,
                        { _: TimePicker, hour: Int, minute: Int ->

                            // calendar3 contains selected time
                            val calendar3 = calendar.clone() as Calendar

                            calendar3.set(Calendar.HOUR_OF_DAY, hour)
                            calendar3.set(Calendar.MINUTE, minute)

                            // check if calendar3 time is valid
                            if (calendar3.timeInMillis < calendar2.timeInMillis) {
                                val minTimeFormatted = formatting.format(calendar2.time)


                                snackScope.launch {
                                    snackState.showSnackbar(
                                        "⚠️ Please select a time at least 1 hour from now.\n⏳ Minimum allowed: $minTimeFormatted"
                                    )
                                }
                            } else {
                                // if valid, change default calendar time to calendar3 time
                                calendar.set(Calendar.HOUR_OF_DAY, hour)
                                calendar.set(Calendar.MINUTE, minute)

                                // snackbar the time
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
                        false
                    )

                    // set these to false to RESTART the time and date picking
                    // in case of dismissal.

                    tpd.setOnCancelListener { showTimePicker = false }
                    tpd.setOnDismissListener { showTimePicker = false }

                    tpd.show()
                }
            }


            item {

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 16.dp)
                ) {
                    Button(
                        onClick = {
                            Log.wtf("DNX", "BEFORE")

                            // init a new finalTaskList, if needed.
                            if (finalTaskList == null) {
                                finalTaskList = TaskList()
                            }

                            finalTaskList?.let { taskList ->
                                taskList.copyTasks(temporaryTaskList.toList())
                                taskList.moneyAmount = moneyAmount.toDoubleOrNull()!!
                                taskList.currency = currency.lowercase()
                                taskList.calendar = calendar
                            }
                            Log.wtf("DNX", finalTaskList.toString())

                            // payment! (the paymentintent middleware currently points towards
                            // the object pointed at by variable finalTaskList
                            triggerPayment = true
                            // navController.navigate("payment")
                        },
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(horizontal = 16.dp),
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


