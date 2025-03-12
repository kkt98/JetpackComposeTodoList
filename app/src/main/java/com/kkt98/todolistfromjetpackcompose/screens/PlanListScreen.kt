package com.kkt98.todolistfromjetpackcompose.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kkt98.todolistfromjetpackcompose.util.SchedulesList
import com.kkt98.todolistfromjetpackcompose.viewmodel.CalenderPlanViewModel
import kotlinx.datetime.LocalDate

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
@Composable
fun PlanListScreen(viewModel: CalenderPlanViewModel = hiltViewModel()) {

    LaunchedEffect(Unit) {
        viewModel.getAllSchedules()
    }

    // ViewModel에서 불러온 모든 일정 데이터
    val schedules by viewModel.schedules.collectAsState()

    // schedules가 변경될 때마다 그룹화 후 오름순 으로 정렬
    val groupedSchedules = remember(schedules) {
        schedules.groupBy { it.date }
            .toSortedMap { date1, date2 ->
                // 날짜 문자열을 LocalDate로 파싱하여 오름순 정렬
                LocalDate.parse(date1).compareTo(LocalDate.parse(date2))
            }
    }

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
            items(items = plans, key = { it.id }) { plan ->
                SchedulesList(schedule = plan, viewModel = viewModel, "allPlan")
            }
        }
    }
}
