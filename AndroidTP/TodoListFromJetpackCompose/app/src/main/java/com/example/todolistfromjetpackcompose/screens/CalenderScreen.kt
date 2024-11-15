package com.example.todolistfromjetpackcompose.screens

import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Checkbox
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TimeInput
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.todolistfromjetpackcompose.util.SchedulesList
import com.example.todolistfromjetpackcompose.viewmodel.CalenderPlanViewModel
import io.github.boguszpawlowski.composecalendar.SelectableCalendar
import io.github.boguszpawlowski.composecalendar.day.DayState
import io.github.boguszpawlowski.composecalendar.rememberSelectableCalendarState
import io.github.boguszpawlowski.composecalendar.selection.DynamicSelectionState
import io.github.boguszpawlowski.composecalendar.selection.SelectionMode
import java.time.LocalDate

@Composable
fun CalenderScreen(viewModel: CalenderPlanViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val schedules by viewModel.schedules.collectAsState()
    val operationStatus by viewModel.operationStatus.collectAsState()
    val savedDates by viewModel.savedDates.collectAsState()

    val savedLocalDates = remember(savedDates) {
        savedDates.mapNotNull { dateStr ->
            try { LocalDate.parse(dateStr) } catch (e: Exception) { null }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.loadSavedDates()
    }

    val calendarState = rememberSelectableCalendarState(
        initialSelectionMode = SelectionMode.Single,
    )

    var showDialog by remember { mutableStateOf(false) }
    var selectedDate = calendarState.selectionState.selection.firstOrNull()

    // 선택된 날짜에 따라 ViewModel에서 일정 가져오기
    LaunchedEffect(selectedDate) {
        selectedDate?.let {
            viewModel.getSchedulesByDate(it.toString())
        }
    }

    Scaffold(
        floatingActionButton = {
            if (selectedDate != null) {
                FloatingActionButton(onClick = { showDialog = true }) {
                    Icon(Icons.Filled.Add, contentDescription = "Add")
                }
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            SelectableCalendar(
                calendarState = calendarState,
                dayContent = { day ->
                    val isCurrentMonth = day.date.month == calendarState.monthState.currentMonth.month
                    val isSelected = day.date == selectedDate // 클릭된 날짜인지 확인


                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .padding(5.dp)
                            .background(
                                color = when{
                                    isSelected -> Color.Gray
                                    else -> Color.Transparent },
                                shape = RoundedCornerShape(12.dp) // 모서리를 둥글게 설정
                            )
                            .then(
                                if (isCurrentMonth) {
                                    Modifier.border(
                                        width = 1.dp,
                                        color = calculateBorderColor(day, savedLocalDates),
                                        shape = RoundedCornerShape(12.dp) // 모서리를 둥글게 설정
                                    )
                                } else Modifier // 이번 달이 아닌 경우 border 제거
                            )
                            .clickable {
                                calendarState.selectionState.onDateSelected(day.date)
                                selectedDate = day.date // 선택된 날짜 업데이트

                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = day.date.dayOfMonth.toString(),
                            color = if (isCurrentMonth) Color.Black else Color.Gray,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            )

            // 일정 목록 표시
            if (selectedDate != null && schedules.isNotEmpty()) {
                Text(
                    text = "일정",
                    modifier = Modifier.padding(8.dp),
                    style = androidx.compose.material.MaterialTheme.typography.h6
                )
                LazyColumn {
                    items(schedules) { schedule ->
                        SchedulesList(schedule = schedule, viewModel = viewModel)
                    }
                }
            }
        }
    }

    // 다이얼로그 표시
    if (showDialog) {
        ScheduleDialog(
            onDismissRequest = { showDialog = false },
            selectedDate = selectedDate,
            onSave = { date, plan, time, alarm ->
                viewModel.insertSchedule(context, date, plan, time, alarm)
                showDialog = false
            },
            isEditMode = false
        )
    }

    LaunchedEffect(operationStatus) {
        operationStatus?.let {
            Handler(Looper.getMainLooper()).post {
                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            }
            viewModel.resetOperationStatus()
        }
    }
}

@Composable
fun calculateBorderColor(day: DayState<DynamicSelectionState>, savedLocalDates: List<LocalDate>): Color {
    return when {
        day.isCurrentDay -> Color.Green // 오늘 날짜는 녹색
        day.date in savedLocalDates -> Color.Blue // 저장된 일정이 있는 날짜는 파란색
        else -> Color.Gray // 기본 테두리 색상
    }
}

// 일정 추가 다이얼로그
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleDialog(
    onDismissRequest: () -> Unit,
    selectedDate: LocalDate?,
    initialText: String = "",
    initialTime: String = "", // 초기 시간 추가
    initialAlarm: Boolean = false, // 초기 알람 상태 추가
    onSave: (String, String, String, Boolean) -> Unit,
    isEditMode: Boolean = false,
) {
    var text by remember { mutableStateOf(initialText) }
    var isAlarmSet by remember { mutableStateOf(initialAlarm) }
    val context = LocalContext.current

    // 초기 시간 설정
    val (initialHour, initialMinute) = if (initialTime.isNotEmpty()) {
        initialTime.split(":").map { it.toIntOrNull() ?: 0 } // 숫자가 아닌 값은 기본값 0으로 처리
    } else {
        listOf(0, 0) // 기본 시간을 0:00으로 설정
    }
    val timePickerState = rememberTimePickerState(
        initialHour = initialHour,
        initialMinute = initialMinute,
        is24Hour = false,
    )

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(if (isEditMode) "일정 수정" else "일정 추가") },
        text = {
            Column {
                Text("날짜: ${selectedDate.toString()}")
                Spacer(
                    Modifier
                        .fillMaxWidth()
                        .height(16.dp))

                // 시간 선택 UI
                TimeInput(state = timePickerState)
                Spacer(
                    Modifier
                        .fillMaxWidth()
                        .height(8.dp))

                // 알람 설정 체크박스
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isAlarmSet,
                        onCheckedChange = { isChecked ->
                            isAlarmSet = isChecked
                        }
                    )
                    Text("알람 설정")
                }

                Spacer(
                    Modifier
                        .fillMaxWidth()
                        .height(8.dp))

                // 텍스트 입력 필드
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
                    val selectedTime = String.format(
                        "%02d:%02d",
                        timePickerState.hour,
                        timePickerState.minute
                    )
                    onSave(it.toString(), text, selectedTime, isAlarmSet)
                }
                onDismissRequest()
            }) {
                Text(if (isEditMode) "수정" else "추가")
            }
        },
        dismissButton = {
            Button(onClick = onDismissRequest) {
                Text("취소")
            }
        }
    )
}
