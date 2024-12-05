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
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Checkbox
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarDefaults.backgroundColor
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
import androidx.compose.ui.res.colorResource
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
    // Context를 가져오기 (ex. Toast 메시지를 표시할 때 사용)
    val context = LocalContext.current

    // ViewModel에서 일정, 작업 상태, 저장된 날짜들을 가져옴
    val schedules by viewModel.schedules.collectAsState() // 일정 데이터
    val operationStatus by viewModel.operationStatus.collectAsState() // 작업 상태 (ex. 성공/실패 메시지)
    val savedDates by viewModel.savedDates.collectAsState() // 저장된 날짜 리스트

    // 저장된 날짜 문자열을 LocalDate 객체로 변환 (잘못된 포맷은 제외)
    val savedLocalDates = remember(savedDates) {
        savedDates.mapNotNull { dateStr ->
            try { LocalDate.parse(dateStr) } catch (e: Exception) { null }
        }
    }

    // 화면이 처음 실행될 때 ViewModel에서 저장된 날짜를 불러옴
    LaunchedEffect(Unit) {
        viewModel.loadSavedDates()
    }

    // 달력의 상태 저장 (선택 모드는 Single 선택만 가능)
    val calendarState = rememberSelectableCalendarState(
        initialSelectionMode = SelectionMode.Single,
    )

    // 다이얼로그를 보여줄지 여부와 선택된 날짜 상태를 저장
    var showDialog by remember { mutableStateOf(false) } // 다이얼로그 표시 여부
    var selectedDate = calendarState.selectionState.selection.firstOrNull() // 선택된 날짜 상태

    // 선택된 날짜가 변경될 때 ViewModel에서 해당 날짜의 일정을 불러옴
    LaunchedEffect(selectedDate) {
        selectedDate?.let {
            viewModel.getSchedulesByDate(it.toString()) // 선택된 날짜로 일정 가져오기
        }
    }

    // 화면의 전체 구조 (Scaffold를 사용해 플로팅 버튼 포함)
    Scaffold(
        floatingActionButton = {
            // 날짜가 선택된 경우 일정 추가 버튼 표시
            if (selectedDate != null) {
                FloatingActionButton(
                    onClick = { showDialog = true },
                    backgroundColor =  Color(0xFFB3E5FC)
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "Add") // 플로팅 버튼 아이콘
                }
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            // 달력 UI 구성
            SelectableCalendar(
                calendarState = calendarState,
                dayContent = { day ->
                    val isCurrentMonth = day.date.month == calendarState.monthState.currentMonth.month // 현재 달 확인
                    val isSelected = day.date == selectedDate // 선택된 날짜인지 확인

                    // 날짜 박스 스타일
                    Box(
                        modifier = Modifier
                            .size(60.dp) // 날짜 크기
                            .padding(5.dp) // 날짜 내부 여백
                            .background(
                                color = when {
                                    isSelected -> Color(0xFFD4D4D5) // 선택된 날짜 배경색
                                    else -> Color.Transparent
                                },
                                shape = RoundedCornerShape(12.dp) // 모서리를 둥글게 설정
                            )
                            .then(
                                if (isCurrentMonth) {
                                    Modifier.border(
                                        width = 1.dp,
                                        color = calculateBorderColor(
                                            day,
                                            savedLocalDates
                                        ), // 테두리 색상 계산
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                } else Modifier // 이번 달이 아닌 경우 테두리 제거
                            )
                            .clickable {
                                calendarState.selectionState.onDateSelected(day.date) // 날짜 선택
                                selectedDate = day.date // 선택된 날짜 업데이트
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = day.date.dayOfMonth.toString(), // 날짜 표시
                            color = if (isCurrentMonth) Color.Black else Color.Gray, // 이번 달/다른 달 텍스트 색상
                            textAlign = TextAlign.Center //가운데 정렬
                        )
                    }
                }
            )

            // 일정 목록 표시
            if (selectedDate != null && schedules.isNotEmpty()) {
                Text(
                    text = "일정",
                    modifier = Modifier.padding(8.dp),
                    style = androidx.compose.material.MaterialTheme.typography.h6 // 제목 스타일
                )
                LazyColumn {
                    items(schedules) { schedule ->
                        SchedulesList(schedule = schedule, viewModel = viewModel) // 일정 아이템 렌더링
                    }
                }
            }
        }
    }

    // 다이얼로그 표시 (일정 추가)
    if (showDialog) {
        ScheduleDialog(
            onDismissRequest = { showDialog = false }, // 다이얼로그 닫기
            selectedDate = selectedDate, // 선택된 날짜 전달
            onSave = { date, plan, time, alarm -> // 일정 저장 콜백
                viewModel.insertSchedule(context, date, plan, time, alarm) // ViewModel에 저장 요청
                showDialog = false // 다이얼로그 닫기
            },
            isEditMode = false // 추가 모드로 설정
        )
    }

    // 작업 상태 변경 시 Toast 메시지 표시
    LaunchedEffect(operationStatus) {
        operationStatus?.let {
            Handler(Looper.getMainLooper()).post {
                Toast.makeText(context, it, Toast.LENGTH_LONG).show() // Toast 표시
            }
            viewModel.resetOperationStatus() // 작업 상태 초기화
        }
    }
}

// 테두리 색상을 계산 (오늘 날짜, 저장된 날짜, 기본 색상)
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
    onDismissRequest: () -> Unit, // 다이얼로그 종료 콜백
    selectedDate: LocalDate?, // 선택된 날짜
    initialText: String = "", // 초기 입력값
    initialTime: String = "", // 초기 시간
    initialAlarm: Boolean = false, // 초기 알람 상태
    onSave: (String, String, String, Boolean) -> Unit, // 저장 콜백
    isEditMode: Boolean = false, // 수정 모드 여부
) {
    // 입력 필드 상태 관리
    var text by remember { mutableStateOf(initialText) } // 일정 내용
    var isAlarmSet by remember { mutableStateOf(initialAlarm) } // 알람 설정 상태
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
        is24Hour = false, // 24시간제 사용 여부
    )

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(if (isEditMode) "일정 수정" else "일정 추가") }, // 다이얼로그 제목
        text = {
            Column {
                Text("날짜: ${selectedDate.toString()}") // 선택된 날짜 표시
                Spacer(
                    Modifier
                        .fillMaxWidth()
                        .height(16.dp)) // 여백 추가

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
                        checked = isAlarmSet, // 체크박스 상태
                        onCheckedChange = { isChecked -> isAlarmSet = isChecked } // 체크 상태 변경
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
                    onValueChange = { text = it }, // 입력값 변경
                    modifier = Modifier.fillMaxWidth(), // 가로 채우기
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    selectedDate?.let {
                        val selectedTime = String.format(
                            "%02d:%02d", // 시간 형식 (HH:mm)
                            timePickerState.hour,
                            timePickerState.minute
                        )
                        onSave(it.toString(), text, selectedTime, isAlarmSet) // 저장 콜백 호출
                    }
                    onDismissRequest() // 다이얼로그 닫기
                },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color(0xFFB3E5FC), // 버튼 배경색 설정
                )
            ) {
                Text(if (isEditMode) "수정" else "추가") // 버튼 텍스트
            }
        },
        dismissButton = {
            Button(
                onClick = onDismissRequest,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color(0xFFB3E5FC), // 버튼 배경색 설정
                )
            ) {
                Text("취소") // 버튼 텍스트
            }
        }

    )
}
