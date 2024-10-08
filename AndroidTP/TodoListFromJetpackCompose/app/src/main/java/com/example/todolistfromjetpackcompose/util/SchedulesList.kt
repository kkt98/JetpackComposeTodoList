package com.example.todolistfromjetpackcompose.util

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
import androidx.compose.material.MaterialTheme
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
import com.example.todolistfromjetpackcompose.viewmodel.CalenderPlanViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

// SchedulesList.kt
// SchedulesList 함수는 일정 목록을 보여주는 컴포저블 함수입니다.
@OptIn(ExperimentalMaterialApi::class) // 실험적 API인 swipeable 기능을 사용하기 위한 어노테이션
@Composable
fun SchedulesList(
    schedules: List<PlanEntity>,  // 화면에 표시할 일정 목록
    viewModel: CalenderPlanViewModel,  // ViewModel을 통해 일정 관리
) {
    var showEditDialog by remember { mutableStateOf(false) }  // 수정 다이얼로그를 표시할지 여부를 기억하는 상태
    var scheduleToEdit by remember { mutableStateOf<PlanEntity?>(null) }  // 수정할 일정 정보를 담는 상태

    LazyColumn {  // LazyColumn은 일정 목록을 스크롤 가능한 리스트 형태로 표시
        items(schedules) { schedule ->  // 각 일정 항목에 대해 아이템 컴포저블 생성
            val swipeableState = rememberSwipeableState(initialValue = 0)  // 스와이프 상태를 기억
            val coroutineScope = rememberCoroutineScope()  // 스와이프 동작을 처리하기 위한 코루틴 범위
            val squareSize = 80.dp  // 수정/삭제 버튼의 크기 설정
            val sizePx = with(LocalDensity.current) { squareSize.toPx() }  // dp 단위를 px로 변환
            val anchors = mapOf(0f to 0, -sizePx * 2 to 1)  // 스와이프 가능한 최대 범위 정의

            Box(
                modifier = Modifier
                    .padding(8.dp)  // 각 일정의 외부 여백
                    .fillMaxWidth()  // 일정 항목이 화면 너비를 모두 차지하게 설정
                    .height(IntrinsicSize.Min)  // 최소 높이 설정
                    .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))  // 외곽선 및 모서리 둥글게 설정
                    .swipeable(
                        state = swipeableState,  // 스와이프 상태 적용
                        anchors = anchors,  // 스와이프 가능한 범위 정의
                        thresholds = { _, _ -> FractionalThreshold(0.5f) },  // 스와이프가 일정 범위를 넘었을 때 동작하도록 설정
                        orientation = Orientation.Horizontal,  // 스와이프 방향은 가로
                        velocityThreshold = 1000.dp  // 스와이프 속도 임계값 설정
                    )
            ) {
                // 스와이프시 나타나는 수정/삭제 버튼
                Row(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)  // 수정/삭제 버튼은 오른쪽에 배치
                        .width(squareSize * 2)  // 버튼의 너비 설정 (수정/삭제 버튼을 두 개 포함)
                ) {
                    // 수정 버튼
                    TextButton(
                        onClick = {
                            coroutineScope.launch {  // 버튼 클릭 시 코루틴에서 동작 처리
                                scheduleToEdit = schedule  // 수정할 일정 설정
                                showEditDialog = true  // 수정 다이얼로그 표시
                                swipeableState.snapTo(0)  // 스와이프 상태를 원래 위치로 되돌림
                            }
                        },
                        modifier = Modifier
                            .width(80.dp)  // 버튼 너비
                            .fillMaxHeight(),  // 버튼 높이는 Row의 높이만큼
                        colors = ButtonDefaults.textButtonColors(Color.Blue),  // 버튼 색상 설정
                        shape = RoundedCornerShape(0.dp)  // 버튼 모양 설정
                    ) {
                        Text(text = "수정", color = Color.White)  // 수정 버튼 텍스트
                    }

                    // 삭제 버튼
                    TextButton(
                        onClick = {
                            coroutineScope.launch {
                                viewModel.deleteSchedule(schedule)  // 일정 삭제 동작 처리
                                swipeableState.snapTo(0)  // 스와이프 상태 초기화
                            }
                        },
                        modifier = Modifier
                            .width(80.dp)  // 삭제 버튼 너비
                            .fillMaxHeight(),  // 버튼 높이는 Row의 높이만큼
                        colors = ButtonDefaults.textButtonColors(Color.Red),  // 버튼 색상 설정
                        shape = RoundedCornerShape(topEnd = 4.dp, bottomEnd = 4.dp)  // 버튼 모양 설정 (모서리 둥글게)
                    ) {
                        Text(text = "삭제", color = Color.White)  // 삭제 버튼 텍스트
                    }
                }

                // 실제 일정 내용을 담고 있는 부분
                Box(
                    modifier = Modifier
                        .offset { IntOffset(swipeableState.offset.value.roundToInt().coerceAtLeast(-sizePx.toInt() * 2), 0) }  // 스와이프에 따라 이동하는 일정 내용
                        .background(Color.White)  // 배경 색상 설정
                        .fillMaxSize()  // Box 크기를 부모 크기만큼 채움
                ) {
                    ScheduleItem(schedule)  // 일정 항목을 표시하는 컴포저블
                }
            }
        }
    }

    // 수정 다이얼로그가 열려있을 때 표시
    if (showEditDialog && scheduleToEdit != null) {
        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")  // 일정 날짜 형식을 지정
        val selectedDate = LocalDate.parse(scheduleToEdit?.date, dateFormatter)  // 수정할 일정의 날짜를 LocalDate로 변환

        ScheduleDialog(
            onDismissRequest = { showEditDialog = false },  // 다이얼로그 닫기
            selectedDate = selectedDate,  // 선택된 날짜를 다이얼로그에 전달
            initialText = scheduleToEdit?.plan ?: "",  // 일정 내용을 다이얼로그에 전달
            onSave = { date, updatedPlan ->  // 수정된 내용을 저장하는 동작 정의
                viewModel.updateSchedule(scheduleToEdit!!.copy(plan = updatedPlan))  // 수정된 일정 정보를 ViewModel에 전달
                showEditDialog = false  // 다이얼로그 닫기
            },
            isEditMode = true  // 수정 모드로 다이얼로그를 표시
        )
    }
}

@Composable
fun ScheduleItem(schedule: PlanEntity) {
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Text("날짜: ${schedule.date}", style = MaterialTheme.typography.subtitle1)
        Spacer(modifier = Modifier.height(8.dp))
        Text("일정: ${schedule.plan}", style = MaterialTheme.typography.body1)
    }

}
