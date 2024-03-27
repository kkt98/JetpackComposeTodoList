package com.example.todolist.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlin.random.Random

@Composable
fun Screen(
    items: List<Data>, // 할 일 목록을 나타내는 Data 객체의 리스트
    onAddItem: (Data) -> Unit, // 새로운 항목을 추가하는 함수. 이 함수는 Data 객체를 인자로 받습니다.
    onRemoveItem: (Data) -> Unit // 항목을 제거하는 함수. 이 함수도 Data 객체를 인자로 받습니다.
) {
    Column { // 세로로 구성 요소를 나열하는 컴포저블
        LazyColumn(modifier = Modifier.weight(1f), // 화면에 맞게 항목들을 동적으로 로딩하는 리스트 뷰
            contentPadding = PaddingValues(8.dp)) { // 컨텐츠 주변에 8dp의 패딩을 추가

            items(items = items) { item -> // 리스트의 각 항목에 대해 반복
                TodoRow(todo = item, // 각 할 일 항목을 나타내는 컴포저블 함수 호출
                    onItemClick = { onRemoveItem(it) }, // 항목 클릭 시 onRemoveItem 함수 호출
                    modifier =  Modifier.fillParentMaxWidth()) // 너비를 부모에 맞춤
            }
        }

        Button(onClick = { onAddItem(RandomData())}, // 클릭 시 onAddItem 함수를 호출하여 새로운 항목을 추가
            modifier = Modifier
                .padding(16.dp) // 버튼 주변에 16dp의 패딩을 추가
                .fillMaxWidth()) { // 너비를 최대로 설정
            Text(text = "Add an Item") // 버튼에 표시될 텍스트
        }
    }
}

@Composable
fun TodoRow(
    todo: Data, // 할 일 데이터를 나타내는 Data 객체. 각 할 일 항목의 정보(예: 작업, 아이콘, ID)를 포함합니다.
    onItemClick: (Data) -> Unit, // 할 일 항목 클릭 시 호출될 콜백 함수. Data 객체를 매개변수로 받아 사용자 정의 동작을 수행합니다.
    modifier: Modifier // 이 UI 요소에 적용할 Modifiers. Modifiers를 통해 외형, 레이아웃, 동작 등을 커스터마이즈할 수 있습니다.
) {

    // 할 일 항목을 나타내는 UI 구성 요소입니다.
    Row( // 가로로 요소를 나열하는 컴포저블 함수. 여기서는 할 일의 텍스트와 아이콘을 가로로 나열합니다.
        modifier = modifier
            .clickable { onItemClick(todo) } // 할 일 항목을 클릭할 수 있게 하며, 클릭 시 onItemClick 콜백 함수를 호출합니다.
            .padding(horizontal = 16.dp, vertical = 16.dp), // 내부 여백을 설정합니다. 가로와 세로 각각 16dp를 적용합니다.
        horizontalArrangement = Arrangement.SpaceBetween // Row 내의 요소들 사이에 공간을 균등하게 배치합니다.
    ) {
        Text(todo.task) // 할 일 항목의 텍스트를 표시합니다. Data 객체의 'task' 속성 값을 사용합니다.

        val iconColor = remember(todo.id) { randomColor() }

        Icon( // 할 일 항목과 연관된 아이콘을 표시합니다.
            imageVector = todo.icons.imageVector, // ImageVector를 통해 아이콘 이미지를 지정합니다. Data 객체에서 아이콘 정보를 가져옵니다.
            tint = iconColor, // 랜덤한 색상을 아이콘의 색상으로 설정합니다.
            contentDescription = stringResource(id = todo.icons.contentDescription) // 아이콘에 대한 접근성 설명. 스크린 리더 등이 사용하는 텍스트입니다.
        )
    }
}
// 랜덤한 색상을 생성하는 함수입니다.
fun randomColor(): Color {
    return Color(
        red = Random.nextInt(256),
        green = Random.nextInt(256),
        blue = Random.nextInt(256),
        alpha = 255
    )
}
