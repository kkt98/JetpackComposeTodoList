package com.example.todolistfromjetpackcompose.screens

import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.todolistfromjetpackcompose.util.SchedulesList
import com.example.todolistfromjetpackcompose.viewmodel.CalenderPlanViewModel
import io.github.boguszpawlowski.composecalendar.SelectableCalendar
import io.github.boguszpawlowski.composecalendar.rememberSelectableCalendarState
import io.github.boguszpawlowski.composecalendar.selection.SelectionMode
import java.time.LocalDate
import java.time.temporal.WeekFields
import java.util.Locale

@Composable
fun CalenderScreen(viewModel: CalenderPlanViewModel = hiltViewModel()) {
    // 현재 Context 가져오기
    val context = LocalContext.current
    // ViewModel에서 수집한 일정 데이터를 상태로 가져옴
    val schedules by viewModel.schedules.collectAsState()
    // 작업 상태를 상태로 가져와서 UI에서 반영 (예: 토스트 메시지)
    val operationStatus by viewModel.operationStatus.collectAsState()
    val savedDates by viewModel.savedDates.collectAsState()

    // 저장된 날짜를 가져오기 위한 로직
    LaunchedEffect(Unit) {
        viewModel.loadSavedDates()
    }


    // 달력 상태를 기억. SelectionMode.Single로 날짜 선택은 단일 모드
    val calendarState = rememberSelectableCalendarState(
        initialSelectionMode = SelectionMode.Single,
    )

    SelectableCalendar(
        calendarState = calendarState,
        dayContent = { day ->
            val isSavedDate = day.date.toString() in savedDates // 저장된 날짜에 해당하는지 확인

            Box(
                modifier = Modifier
                    .padding(2.dp)
                    .background(
                        if (isSavedDate) Color(0xFF202E9C) else Color.Transparent, // 저장된 날짜일 때 다른 색상 적용
                        shape = RoundedCornerShape(1.dp)
                    )
                    .fillMaxSize()
                    .clickable { calendarState.selectionState.onDateSelected(day.date) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = day.date.dayOfMonth.toString(),
                    color = if (isSavedDate) Color.Black else Color.Gray // 텍스트 색상 변경
                )
            }
        }
    )
    // 다이얼로그를 표시할지 여부를 기억하는 상태
    var showDialog by remember { mutableStateOf(false) }
    // 선택된 날짜를 가져옴 (Single 모드이므로 첫 번째 선택된 날짜만 가져옴)
    val selectedDate = calendarState.selectionState.selection.firstOrNull()

    // 선택된 날짜에 따라 ViewModel에서 해당 날짜의 일정을 불러옴
    viewModel.getSchedulesByDate(
        calendarState.selectionState.selection.joinToString { it.toString() }
    )

    // 화면의 레이아웃을 정의하는 Scaffold
    Scaffold(
        // 플로팅 액션 버튼(FAB)은 선택된 날짜가 있을 때만 표시됨
        floatingActionButton = {
            if (selectedDate != null) {
                FloatingActionButton(onClick = { showDialog = true }) {
                    // FAB 아이콘을 추가 아이콘으로 설정
                    Icon(Icons.Filled.Add, contentDescription = "Add")
                }
            }
        }
    ) {
        // Column은 수직 레이아웃을 제공
        Column(
            modifier = Modifier.padding(it)  // Column 레이아웃의 내부 여백 설정
        ) {
            // 달력 컴포넌트를 다시 표시
            SelectableCalendar(calendarState = calendarState)

            // 선택된 날짜가 있을 경우에만 일정 리스트를 보여줌
            if (selectedDate != null) {
                // 선택된 날짜에 해당하는 일정 목록을 표시하는 컴포저블
                ScheduleListScreen(viewModel)
            }

            // 다이얼로그가 표시되어야 하는 경우
            if (showDialog) {
                ScheduleDialog(
                    onDismissRequest = { showDialog = false },  // 다이얼로그 닫기
                    selectedDate = selectedDate,  // 선택된 날짜를 다이얼로그에 전달
                    onSave = { date, plan, time, alarm ->  // 저장 버튼 클릭 시 동작 정의
                        viewModel.insertSchedule(context, date, plan, time, alarm)  // 새로운 일정 추가
                    },
                    isEditMode = false, // 새 일정을 추가하는 모드
                )
            }
        }
    }

    // 작업 상태 변경 시 토스트 메시지를 보여주는 효과
    LaunchedEffect(operationStatus) {
        operationStatus?.let {
            // 메인 스레드에서 Toast 실행
            Handler(Looper.getMainLooper()).post {
                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            }
            // 토스트 메시지 후 작업 상태 초기화
            viewModel.resetOperationStatus()
        }
    }
}

// 일정 리스트
@Composable
fun ScheduleListScreen(viewModel: CalenderPlanViewModel = hiltViewModel()) {
    val schedules by viewModel.schedules.collectAsState()

    Scaffold { paddingValues ->  // paddingValues 추가
        Column(modifier = Modifier.padding(paddingValues)) {  // paddingValues 적용
            LazyColumn {  // LazyColumn을 통해 일정 목록을 렌더링
                items(schedules) { schedule ->  // 각 일정 항목을 개별적으로 처리
                    Log.d("saveschedle", schedule.toString())

                    SchedulesList(schedule = schedule, viewModel = viewModel)  // 스와이프 가능한 아이템으로 처리
                }
            }
        }
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
                Spacer(Modifier.fillMaxWidth().height(16.dp))

                // 시간 선택 UI
                TimeInput(state = timePickerState)
                Spacer(Modifier.fillMaxWidth().height(8.dp))

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

                Spacer(Modifier.fillMaxWidth().height(8.dp))

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
