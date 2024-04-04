package com.example.todolistfromjetpackcompose.screens

import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.todolistfromjetpackcompose.ui.theme.TodoListFromJetpackComposeTheme
import com.example.todolistfromjetpackcompose.viewmodel.CalenderPlanViewModel
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.boguszpawlowski.composecalendar.SelectableCalendar
import io.github.boguszpawlowski.composecalendar.StaticCalendar
import io.github.boguszpawlowski.composecalendar.day.DayState
import io.github.boguszpawlowski.composecalendar.rememberSelectableCalendarState
import io.github.boguszpawlowski.composecalendar.selection.DynamicSelectionState
import io.github.boguszpawlowski.composecalendar.selection.SelectionMode
import java.time.LocalDate
import java.time.YearMonth

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalenderScreen(viewModel: CalenderPlanViewModel = hiltViewModel()) {

    val context = LocalContext.current
    val saveSuccess by viewModel.saveSuccess.collectAsState()
    val schedules by viewModel.schedules.collectAsState()

    LaunchedEffect(saveSuccess) {
        if (saveSuccess) {
            Toast.makeText(context, "저장 성공", Toast.LENGTH_SHORT).show()
        }
    }

    val calendarState = rememberSelectableCalendarState(
        initialSelectionMode = SelectionMode.Single,
    )
    var showDialog by remember { mutableStateOf(false) }
    val selectedDate = calendarState.selectionState.selection.firstOrNull()


    LaunchedEffect(selectedDate) {
        selectedDate?.let {
            viewModel.getSchedulesByDate(it.toString())
        }
    }

    schedules.forEach {
        Log.d("Asdasdasd", it.plan)
    }

    Scaffold(
        floatingActionButton = {
            if (selectedDate != null) { // 날짜가 선택되었을 때만 버튼 표시
                FloatingActionButton(onClick = { showDialog = true }) {
                    Icon(Icons.Filled.Add, contentDescription = "Add")
                }
            }
        }
    ) {
        Column(
            Modifier
                .verticalScroll(rememberScrollState())
                .padding(it)
        ) {
            SelectableCalendar(
                calendarState = calendarState,
            )

            if (showDialog) {
                ScheduleDialog(
                    onDismissRequest = { showDialog = false },
                    selectedDate = selectedDate,
                    onSave = { date, plan ->
                        viewModel.insertSchedule(date, plan)
                    }
                )
            }
        }
    }
}

@Composable
fun ScheduleDialog(
    onDismissRequest: () -> Unit,
    selectedDate: java.time.LocalDate?,
    onSave: (String, String) -> Unit // 일정 저장 콜백 추가
) {
    var text by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("일정 추가") },
        text = {
            Column {
                Text("선택된 날자: ${selectedDate.toString()}")
                Spacer(Modifier.fillMaxWidth().height(16.dp))
                TextField(
                    value = text,
                    onValueChange = { text = it },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                selectedDate?.let {
                    onSave(it.toString(), text) // 일정 저장
                }
                onDismissRequest()
            }) {
                Text("추가")
            }
        },
        dismissButton = {
            Button(onClick = onDismissRequest) {
                Text("취소")
            }
        }
    )
}

@Composable
private fun SelectionControls(
    selectionState: DynamicSelectionState,
) {
    Text(
        text = "Calendar Selection Mode",
        style = MaterialTheme.typography.h5,
    )
    SelectionMode.values().forEach { selectionMode ->
        Row(modifier = Modifier.fillMaxWidth()) {
            RadioButton(
                selected = selectionState.selectionMode == selectionMode,
                onClick = { selectionState.selectionMode = selectionMode }
            )
            Text(text = selectionMode.name)
            Spacer(modifier = Modifier.height(4.dp))
        }
    }

    Text(
        text = "Selection: ${selectionState.selection.joinToString { it.toString() }}",
        style = MaterialTheme.typography.h6,
    )
}