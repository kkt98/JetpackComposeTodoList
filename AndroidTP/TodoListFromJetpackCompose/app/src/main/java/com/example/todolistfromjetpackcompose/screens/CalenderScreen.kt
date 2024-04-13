package com.example.todolistfromjetpackcompose.screens

import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import kotlin.math.roundToInt

@Composable
fun CalenderScreen(viewModel: CalenderPlanViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val saveSuccess by viewModel.saveSuccess.collectAsState()
    val schedules by viewModel.schedules.collectAsState()

    val calendarState = rememberSelectableCalendarState(
        initialSelectionMode = SelectionMode.Single,
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
                    }
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SchedulesList(schedules: List<PlanEntity>, viewModel: CalenderPlanViewModel) {
    LazyColumn {
        items(schedules) { schedule ->
            val swipeableState = rememberSwipeableState(initialValue = 0)
            val coroutineScope = rememberCoroutineScope()
            val squareSize = 80.dp
            val sizePx = with(LocalDensity.current) { squareSize.toPx() }
            val anchors = mapOf(0f to 0, -sizePx to 1)

            Box(
                modifier = Modifier
                    .padding(8.dp)  // 상위 박스 주변에 패딩 추가
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
                    .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))  // 경계선 추가
                    .swipeable(
                        state = swipeableState,
                        anchors = anchors,
                        thresholds = { _, _ -> FractionalThreshold(0.5f) },
                        orientation = Orientation.Horizontal,
                        velocityThreshold = 1000.dp
                    )
            ) {
                // 삭제 버튼을 포함하는 Box를 정의합니다. 오른쪽 끝에 위치합니다.
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                ) {
                    TextButton(
                        onClick = {
                            coroutineScope.launch {
//                                swipeableState.animateTo(0, tween(600, 0))
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

                // 스와이프로 오프셋이 조정된 상태에서 카드 위치를 다시 조정하는 Box를 정의합니다.
                Box(
                    modifier = Modifier
                        .padding(horizontal = 8.dp)  // 내부 컨텐츠가 테두리 안에서만 스와이프되도록 패딩 추가
                        .offset { IntOffset(swipeableState.offset.value.roundToInt(), 0) }
                        .background(Color.White)
                        .fillParentMaxWidth()
                ) {
                    // 스케줄 아이템을 표시하는 사용자 정의 Composable을 호출합니다.
                    ScheduleItem(schedule)
                }
            }
        }
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