package com.example.todolistfromjetpackcompose.screens

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material.swipeable
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.todolistfromjetpackcompose.room.PlanEntity
import com.example.todolistfromjetpackcompose.util.SchedulesList
import com.example.todolistfromjetpackcompose.viewmodel.CalenderPlanViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
@Composable
fun PlanListScreen(viewModel: CalenderPlanViewModel = hiltViewModel()) {

    LaunchedEffect(Unit) {
        viewModel.getAllSchedules()
    }

    // ViewModel에서 불러온 모든 일정 데이터
    val schedules by viewModel.schedules.collectAsState()

    // 일정 데이터를 날짜별로 그룹화
    val groupedSchedules = schedules.groupBy { it.date } // 날짜별로 그룹화

    LazyColumn(
        modifier = Modifier
            .fillMaxSize() // LazyColumn이 상위 레이아웃의 크기에 맞춰 확장되게 합니다.
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 그룹화된 일정 데이터를 날짜별로 stickyHeader와 함께 보여줌
        groupedSchedules.forEach { (date, plans) ->
            stickyHeader {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.LightGray
                ) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        text = date, // 날짜를 헤더로 표시
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )
                }
            }
            // 해당 날짜의 일정을 표시
            items(plans) { plan ->  // plans는 List<PlanEntity>
                SchedulesList(schedule = plan, viewModel = viewModel)  // 스와이프 가능한 아이템으로 처리
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SwipeableScheduleItem(
    schedule: PlanEntity,
    viewModel: CalenderPlanViewModel = hiltViewModel()
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

        // 실제 일정 내용을 담고 있는 부분
        Box(
            modifier = Modifier
                .offset { IntOffset(swipeableState.offset.value.roundToInt().coerceAtLeast(-sizePx.toInt() * 2), 0) }
                .background(Color.White)
                .fillMaxSize()
        ) {
            ScheduleItem(schedule)
        }
    }

    // 수정 다이얼로그가 열려있을 때 표시
    if (showEditDialog) {
        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val selectedDate = LocalDate.parse(schedule.date, dateFormatter)

        ScheduleDialog(
            onDismissRequest = { showEditDialog = false },
            selectedDate = selectedDate,
            initialText = schedule.plan,
            onSave = { date, updatedPlan ->
                viewModel.updateSchedule(schedule.copy(plan = updatedPlan))
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
        Text("날짜: ${schedule.date}")
        Spacer(modifier = Modifier.height(8.dp))
        Text("일정: ${schedule.plan}")
    }
}

