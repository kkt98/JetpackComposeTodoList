package com.example.todolistfromjetpackcompose.util

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material.Text
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
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.todolistfromjetpackcompose.room.PlanEntity
import com.example.todolistfromjetpackcompose.screens.ScheduleDialog
import com.example.todolistfromjetpackcompose.screens.ScheduleItem
import com.example.todolistfromjetpackcompose.viewmodel.CalenderPlanViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

// SchedulesList.kt
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
