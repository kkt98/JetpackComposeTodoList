package com.example.todolistfromjetpackcompose.screens

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
    val saveSuccess by viewModel.saveSuccess.collectAsState()

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

    // saveSuccess 상태를 관찰하고 true가 되면 토스트를 표시합니다
//    SideEffect {
//        if (saveSuccess) {
//            Toast.makeText(context, "저장 완료", Toast.LENGTH_SHORT).show()
//            viewModel.resetSaveSuccess() // saveSuccess 상태를 초기화합니다
//        }
//    }

    //일정 추가 버튼 누르면 다이얼로그 띄우기
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

            // 일정 추가 버튼을 눌렀을 때
            if (showDialog) {
                ScheduleDialog(
                    onDismissRequest = { showDialog = false },
                    selectedDate = selectedDate,
                    onSave = { date, plan ->
                        viewModel.insertSchedule(date, plan)
                    },
                    isEditMode = false // 추가 모드
                )
            }

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
    var scheduleToEdit by remember { mutableStateOf<PlanEntity?>(null) } // 수정할 일정 저장

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
                                scheduleToEdit = schedule // 수정할 일정 저장
                                showEditDialog = true // 수정 다이얼로그 표시
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
                        .offset { IntOffset(swipeableState.offset.value.roundToInt(), 0) }
                        .background(Color.White)
                        .fillMaxSize()
                ) {
                    ScheduleItem(schedule)
                }
            }
        }
    }

    // 수정 다이얼로그가 표시되어야 할 때
    if (showEditDialog && scheduleToEdit != null) {
        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd") // 변환을 위한 포맷터
        val selectedDate = LocalDate.parse(scheduleToEdit?.date, dateFormatter) // String -> LocalDate 변환

        ScheduleDialog(
            onDismissRequest = { showEditDialog = false },
            selectedDate = selectedDate, // 변환된 LocalDate 전달
            initialText = scheduleToEdit?.plan ?: "", // 기존 일정 내용
            onSave = { date, updatedPlan ->
                viewModel.updateSchedule(scheduleToEdit!!.copy(plan = updatedPlan))
                showEditDialog = false
            },
            isEditMode = true // 수정 모드
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

