package com.example.todolistfromjetpackcompose.screens

import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material.swipeable
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.todolistfromjetpackcompose.room.PlanEntity
import com.example.todolistfromjetpackcompose.viewmodel.CalenderPlanViewModel
import io.github.boguszpawlowski.composecalendar.SelectableCalendar
import io.github.boguszpawlowski.composecalendar.rememberSelectableCalendarState
import io.github.boguszpawlowski.composecalendar.selection.SelectionMode
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun CalenderScreen(viewModel: CalenderPlanViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val schedules by viewModel.schedules.collectAsState()
    val operationStatus by viewModel.operationStatus.collectAsState() // 작업 상태를 감지

    val calendarState = rememberSelectableCalendarState(
        initialSelectionMode = SelectionMode.Single,
    )

    SelectableCalendar(
        calendarState = calendarState,
        firstDayOfWeek = WeekFields.of(Locale.KOREAN).firstDayOfWeek,
    )

    var showDialog by remember { mutableStateOf(false) }
    val selectedDate = calendarState.selectionState.selection.firstOrNull()

    viewModel.getSchedulesByDate(

        calendarState.selectionState.selection.joinToString { it.toString() }
    )

    Scaffold(
        floatingActionButton = {
            if (selectedDate != null) {
                FloatingActionButton(onClick = { showDialog = true }) {
                    Icon(Icons.Filled.Add, contentDescription = "Add")
                }
            }
        }
    ) {
        Column(
            modifier = Modifier.padding(it)
        ) {
            SelectableCalendar(calendarState = calendarState)
            if (selectedDate != null) {
                SchedulesList(schedules, viewModel)
            }

            if (showDialog) {
                ScheduleDialog(
                    onDismissRequest = { showDialog = false },
                    selectedDate = selectedDate,
                    onSave = { date, plan ->
                        viewModel.insertSchedule(date, plan)
                    },
                    isEditMode = false
                )
            }
        }
    }

    // 작업 완료 시 토스트 메시지 표시
    LaunchedEffect(operationStatus) {
        operationStatus?.let {
            Log.d("asdasdas", it + "0")
// 메인 스레드에서 Toast 실행
            Handler(Looper.getMainLooper()).post {
                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            }
            Log.d("asdasdas", it + "1")
            viewModel.resetOperationStatus() // 토스트 후 상태 초기화
            Log.d("asdasdas", it + "2")
        }
    }
}



@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SchedulesList(
    schedules: List<PlanEntity>,
    viewModel: CalenderPlanViewModel,
) {
    var showEditDialog by remember { mutableStateOf(false) }
    var scheduleToEdit by remember { mutableStateOf<PlanEntity?>(null) }

    LazyColumn {
        items(schedules) { schedule ->
            val swipeableState = rememberSwipeableState(initialValue = 0)
            val coroutineScope = rememberCoroutineScope()
            val squareSize = 80.dp
            val sizePx = with(LocalDensity.current) { squareSize.toPx() }
            val anchors = mapOf(0f to 0, -sizePx * 2 to 1)

            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
                    .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
                    .swipeable(
                        state = swipeableState,
                        anchors = anchors,
                        thresholds = { _, _ -> FractionalThreshold(0.5f) },
                        orientation = Orientation.Horizontal,
                        velocityThreshold = 1000.dp
                    )
            ) {
                Row(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .width(squareSize * 2)
                ) {
                    // 수정 버튼
                    TextButton(
                        onClick = {
                            coroutineScope.launch {
                                scheduleToEdit = schedule
                                showEditDialog = true
                                swipeableState.snapTo(0)
                            }
                        },
                        modifier = Modifier
                            .width(80.dp)
                            .fillMaxHeight(),
                        colors = ButtonDefaults.textButtonColors(Color.Blue),
                        shape = RoundedCornerShape(0.dp)
                    ) {
                        Text(text = "수정", color = Color.White)
                    }

                    // 삭제 버튼
                    TextButton(
                        onClick = {
                            coroutineScope.launch {
                                viewModel.deleteSchedule(schedule)
                                swipeableState.snapTo(0)
                            }
                        },
                        modifier = Modifier
                            .width(80.dp)
                            .fillMaxHeight(),
                        colors = ButtonDefaults.textButtonColors(Color.Red),
                        shape = RoundedCornerShape(topEnd = 4.dp, bottomEnd = 4.dp)
                    ) {
                        Text(text = "삭제", color = Color.White)
                    }
                }

                Box(
                    modifier = Modifier
                        .offset { IntOffset(swipeableState.offset.value.roundToInt().coerceAtLeast(-sizePx.toInt() * 2), 0) } // 제한된 스와이프 범위
                        .background(Color.White)
                        .fillMaxSize()
                ) {
                    ScheduleItem(schedule)
                }
            }
        }
    }

    if (showEditDialog && scheduleToEdit != null) {
        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val selectedDate = LocalDate.parse(scheduleToEdit?.date, dateFormatter)

        ScheduleDialog(
            onDismissRequest = { showEditDialog = false },
            selectedDate = selectedDate,
            initialText = scheduleToEdit?.plan ?: "",
            onSave = { date, updatedPlan ->
                viewModel.updateSchedule(scheduleToEdit!!.copy(plan = updatedPlan))
                showEditDialog = false
            },
            isEditMode = true
        )
    }
}



@Composable
fun ScheduleItem(schedule: PlanEntity) {
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Text("날자: ${schedule.date}", style = MaterialTheme.typography.subtitle1)
        Spacer(modifier = Modifier.height(8.dp))
        Text("일정: ${schedule.plan}", style = MaterialTheme.typography.body1)
    }

}

@Composable
fun ScheduleDialog(
    onDismissRequest: () -> Unit,
    selectedDate: LocalDate?,
    initialText: String = "", // 기존 텍스트를 받아서 초기화
    onSave: (String, String) -> Unit, // 저장 콜백
    isEditMode: Boolean = false // 수정 모드인지 추가 모드인지 구분
) {
    var text by remember { mutableStateOf(initialText) } // 초기 텍스트 설정
    val context = LocalContext.current // LocalContext로 context 가져오기

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(if (isEditMode) "일정 수정" else "일정 추가") }, // 다이얼로그 제목 변경
        text = {
            Column {
                Text("날짜: ${selectedDate.toString()}")
                Spacer(
                    Modifier
                        .fillMaxWidth()
                        .height(16.dp)
                )
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
                    onSave(it.toString(), text) // 저장 시 콜백 호출
                }
//                Toast.makeText(context, if (isEditMode) "수정 완료" else "추가 완료", Toast.LENGTH_LONG).show()
                onDismissRequest()
            }) {
                Text(if (isEditMode) "수정" else "추가") // 버튼 텍스트 변경
            }
        },
        dismissButton = {
            Button(onClick = onDismissRequest) {
                Text("취소")
            }
        }
    )
}