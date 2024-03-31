package com.example.todolist.ui

import androidx.annotation.StringRes
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp

@Composable
fun TodoInputText(
    text: String, // 입력 필드에 표시되는 텍스트
    onTextChange:(String) -> Unit, // 텍스트가 변경될 때 호출되는 콜백 함수
    modifier: Modifier, // 입력 필드에 적용되는 Modifier
    onImeAction: () -> Unit={} // 입력 필드에서 IME 액션(예: Enter 키 또는 Done 버튼)을 수행할 때 호출되는 콜백 함수
) {
    // 키보드 컨트롤러를 가져옵니다.
    val keyBoardController = LocalSoftwareKeyboardController.current

    // TextField를 사용하여 입력 필드를 정의합니다.
    TextField(
        value = text, // 현재 텍스트 값
        onValueChange = onTextChange, // 텍스트가 변경될 때 호출되는 콜백 함수
//        colors = TextFieldDefaults.colors(Color.Transparent), // 입력 필드의 색상을 투명으로 설정
        maxLines = 2, // 입력 필드의 최대 줄 수
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done), // 키보드 옵션 설정
        keyboardActions = KeyboardActions(onDone = { onImeAction() // Done 버튼 또는 Enter 키를 눌렀을 때 호출되는 콜백 함수
            keyBoardController?.hide()}), // 키보드 숨기기
        modifier = modifier // Modifier 적용
    )
}

@Composable
fun TodoEditButton(
    onClick: () -> Unit, // 버튼 클릭 시 호출되는 콜백 함수
    text: String, // 버튼에 표시되는 텍스트
    modifier: Modifier = Modifier, // 버튼에 적용되는 Modifier
    enable: Boolean =true, // 버튼 활성화 여부
){
    // TextButton을 사용하여 버튼을 정의합니다.
    TextButton(
        onClick = onClick, // 클릭 시 호출되는 콜백 함수
        shape = CircleShape, // 버튼의 모양을 원형으로 설정
        enabled = enable, // 버튼의 활성화 여부
        modifier = modifier // Modifier 적용
    ) {
        Text(text = text) // 버튼에 표시되는 텍스트
    }
}

@Composable
fun TodoInputBackground(
    elevate: Boolean, // 배경의 고도 여부
    modifier: Modifier=Modifier, // 배경에 적용되는 Modifier
    content: @Composable RowScope.() -> Unit // 배경 내부의 컨텐츠를 정의하는 lambda 표현식
) {
    // 배경의 고도를 애니메이션으로 설정합니다.
    val animatedElevation by animateDpAsState(
        if (elevate) 1.dp else 0.dp, // 배경이 떠오르는 효과가 있는지 여부에 따라 고도를 변경합니다.
        TweenSpec(500), // 애니메이션 지속 시간 설정
        label = "aaaa" // 애니메이션 라벨 설정
    )

    // Surface를 사용하여 배경을 정의합니다.
    Surface(
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = .05f), // 배경의 색상과 투명도 설정
        tonalElevation = animatedElevation, // 배경의 고도 설정
        shape = RectangleShape, // 배경의 모양 설정
    ) {
        // Row를 사용하여 배경 내부의 컨텐츠를 정의합니다.
        Row(
            modifier = modifier.animateContentSize(TweenSpec(300)), // Modifier 적용 및 크기 애니메이션 설정
            content = content // 배경 내부의 컨텐츠를 정의하는 lambda 표현식
        )
    }
}

@Composable
fun SelectableButton(
    icons: ImageVector, // 사용할 아이콘
    @StringRes iconContentDescription: Int, // 아이콘의 내용 설명 문자열 리소스 ID
    onIconSelected: () -> Unit, // 아이콘 선택 시 호출될 콜백 함수
    isSelected: Boolean, // 아이콘이 선택되었는지 여부를 나타내는 플래그
    modifier: Modifier = Modifier // 버튼에 적용할 Modifier
) {
    // 선택된 경우 Material 테마의 주요 색상을 아이콘에 적용하고,
    // 선택되지 않은 경우 Surface의 텍스트 색상의 투명도를 조절하여 사용합니다.
    val tint = if (isSelected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onSurface.copy(alpha = .6f)
    }

    // 아이콘을 포함하는 TextButton을 렌더링합니다.
    TextButton(
        onClick = { onIconSelected() }, // 아이콘을 클릭하면 onIconSelected 콜백 함수가 호출됩니다.
        shape = RectangleShape, // 버튼의 모양을 사각형으로 설정합니다.
        modifier = modifier // 외부에서 제공된 Modifier를 적용합니다.
    ) {
        Column {
            // 아이콘을 표시합니다.
            Icon(
                imageVector = icons,
                contentDescription = stringResource(id = iconContentDescription) // 아이콘에 대한 접근성 설명을 제공합니다.
            )
            // 선택된 경우, 선택된 상태를 시각적으로 나타내기 위해 아이콘 아래에 선을 그립니다.
            if (isSelected) {
                Box(
                    modifier = modifier
                        .padding(top = 3.dp) // 선과 아이콘 사이의 간격을 설정합니다.
                        .width(icons.defaultWidth) // 선의 너비를 아이콘의 너비로 설정합니다.
                        .height(1.dp) // 선의 높이를 1dp로 설정합니다.
                        .background(tint) // 선의 배경색을 tint로 설정합니다.
                )
            } else { // 선택되지 않은 경우, 추가적인 공간을 확보하기 위해 Spacer를 추가합니다.
                Spacer(
                    modifier = modifier.height(4.dp) // 4dp의 높이를 가진 Spacer를 추가합니다.
                )
            }
        }
    }
}

@Composable
fun IconRow(
    icon: ToDoIcons, // 현재 선택된 아이콘
    onIconChange: (ToDoIcons) -> Unit, // 아이콘 변경 시 호출될 콜백 함수
    modifier: Modifier = Modifier // Row에 적용할 Modifier
) {
    Row(modifier) { // 아이콘을 나열하는 가로로 정렬된 Row를 생성합니다.
        for (todoIcons in ToDoIcons.values()) { // ToDoIcons의 모든 아이콘을 반복합니다.
            // 각 아이콘에 대해 SelectableButton을 호출하여 선택 가능한 버튼을 렌더링합니다.
            SelectableButton(
                icons = todoIcons.imageVector, // 아이콘 이미지
                iconContentDescription = icon.contentDescription, // 아이콘의 내용 설명 문자열 리소스 ID
                onIconSelected = { onIconChange(todoIcons) }, // 아이콘을 선택하면 해당 아이콘으로 변경됩니다.
                isSelected = todoIcons == icon // 현재 아이콘이 선택된 상태인지 여부를 확인합니다.
            )
        }
    }
}

