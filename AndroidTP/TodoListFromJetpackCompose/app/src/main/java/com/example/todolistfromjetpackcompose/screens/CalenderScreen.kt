package com.example.todolistfromjetpackcompose.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.todolistfromjetpackcompose.Greeting
import com.example.todolistfromjetpackcompose.ui.theme.TodoListFromJetpackComposeTheme
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.time.temporal.WeekFields
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalenderScreen(yearMonth: YearMonth) {
    val daysOfWeek = listOf("일", "월", "화", "수", "목", "금", "토")
    val firstDayOfMonth = yearMonth.atDay(1).with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.SUNDAY))
    val lastDayOfMonth = yearMonth.atEndOfMonth().with(TemporalAdjusters.nextOrSame(java.time.DayOfWeek.SATURDAY))
    var currentDay = firstDayOfMonth
    val dates = mutableListOf<LocalDate>()

    while (currentDay.isBefore(lastDayOfMonth) || currentDay.isEqual(lastDayOfMonth)) {
        dates.add(currentDay)
        currentDay = currentDay.plusDays(1)
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = yearMonth.format(DateTimeFormatter.ofPattern("yyyy MMMM")),
                style = MaterialTheme.typography.bodyLarge
            )
        }

        LazyVerticalGrid(columns = GridCells.Fixed(7)) {
            items(daysOfWeek) { dayOfWeek ->
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .padding(4.dp)
                        .height(48.dp)
                ) {
                    Text(text = dayOfWeek)
                }
            }
        }

        LazyVerticalGrid(columns = GridCells.Fixed(7)) {
            items(dates) { date ->
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .padding(4.dp)
                        .height(48.dp)
                ) {
                    if (date.month == yearMonth.month) {
                        Text(text = date.dayOfMonth.toString())
                    } else {
                        // 이전 또는 다음 달의 날짜는 표시하지 않음
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TodoListFromJetpackComposeTheme {
        CalenderScreen(YearMonth.now())
    }
}