package com.kkt98.todolistfromjetpackcompose.util

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material.swipeable
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kkt98.todolistfromjetpackcompose.R
import com.kkt98.todolistfromjetpackcompose.room.PlanEntity
import com.kkt98.todolistfromjetpackcompose.screens.ScheduleDialog
import com.kkt98.todolistfromjetpackcompose.viewmodel.CalenderPlanViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

// SchedulesList.kt
// SchedulesList 함수는 일정 목록을 보여주는 컴포저블 함수입니다.
@OptIn(ExperimentalMaterialApi::class) // 실험적 API인 swipeable 기능을 사용하기 위한 어노테이션
@Composable
fun SchedulesList(
    schedule: PlanEntity,
    viewModel: CalenderPlanViewModel = hiltViewModel(),
    plan: String
) {
    var showEditDialog by remember { mutableStateOf(false) }  // 수정 다이얼로그를 표시할지 여부를 기억하는 상태
    val coroutineScope = rememberCoroutineScope()
    val squareSize = 80.dp
    val sizePx = with(LocalDensity.current) { squareSize.toPx() }
    val swipeableState = rememberSwipeableState(initialValue = 0)
    val anchors = mapOf(0f to 0, -sizePx * 2 to 1) // 스와이프 가능한 최대 범위 정의

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
        // 스와이프시 나타나는 수정/삭제 버튼
        Row(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .width(squareSize * 2)

        ) {
            // 수정 버튼
            TextButton(
                onClick = {
                    coroutineScope.launch {
                        showEditDialog = true // 수정 다이얼로그 표시
                        swipeableState.snapTo(0)
                    }
                },
                enabled = swipeableState.offset.value != 0f, // 스와이프 상태에 따라 클릭 비활성화
                modifier = Modifier
                    .width(80.dp)
                    .fillMaxHeight(),
                colors = ButtonDefaults.textButtonColors(Color.Blue),
                shape = RoundedCornerShape(0.dp)
            ) {
                androidx.compose.material3.Text(text = "수정", color = Color.White)
            }

            // 삭제 버튼
            TextButton(
                onClick = {
                    coroutineScope.launch {
                        viewModel.deleteSchedule(schedule, plan) // 일정 삭제 기능 호출
                        swipeableState.snapTo(0)
                    }
                },
                enabled = swipeableState.offset.value != 0f, // 스와이프 상태에 따라 클릭 비활성화
                modifier = Modifier
                    .width(80.dp)
                    .fillMaxHeight(),
                colors = ButtonDefaults.textButtonColors(Color.Red),
                shape = RoundedCornerShape(topEnd = 4.dp, bottomEnd = 4.dp)
            ) {
                androidx.compose.material3.Text(text = "삭제", color = Color.White)
            }
        }

        // 실제 일정 내용을 담고 있는 부분
        Box(
            modifier = Modifier
                .offset {
                    IntOffset(
                        swipeableState.offset.value
                            .roundToInt()
                            .coerceAtLeast(-sizePx.toInt() * 2), 0
                    )
                }
                .background(Color.White)
                .fillMaxSize()
        ) {
            ScheduleItem(schedule) // 일정 아이템 표시
        }
    }

    // 수정 다이얼로그가 열려있을 때 표시
    if (showEditDialog) {
        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val selectedDate = LocalDate.parse(schedule.date, dateFormatter)

        ScheduleDialog(
            onDismissRequest = { showEditDialog = false }, // 다이얼로그 닫기
            selectedDate = selectedDate,
            initialText = schedule.plan, // 기존 일정 내용 전달
            initialTime = schedule.time, // 기존 시간 전달
            initialAlarm = schedule.alarm, // 기존 알람 상태 전달
            onSave = { date, updatedPlan, updatedTime, alarmSet -> // 수정 후 저장 콜백
                viewModel.updateSchedule(
                    schedule.copy(plan = updatedPlan, time = updatedTime, alarm = alarmSet)
                    , plan
                )
                showEditDialog = false
            },
            isEditMode = true, // 수정 모드 설정
        )
    }
}

// ScheduleItem 함수는 단일 일정 항목을 표시합니다.
@Composable
fun ScheduleItem(schedule: PlanEntity) {
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        // 상단에 날짜와 알람 아이콘 표시
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            androidx.compose.material3.Text("날짜: ${schedule.date}")
            Spacer(modifier = Modifier.weight(1f)) // 빈 공간으로 아이콘을 오른쪽 끝으로 밀어냄
            Image(
                painter = painterResource(id = if (schedule.alarm) R.drawable.baseline_alarm_on_24 else R.drawable.baseline_alarm_off_24),
                contentDescription = "bell Icon",
                modifier = Modifier.size(24.dp))
        }

        Spacer(modifier = Modifier.height(8.dp))
        androidx.compose.material3.Text("시간: ${schedule.time}") // 일정 시간 표시
        Spacer(modifier = Modifier.height(8.dp))
        androidx.compose.material3.Text("일정: ${schedule.plan}") // 일정 내용 표시
    }
}
