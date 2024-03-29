package com.example.todolist.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.todolist.TodoViewModel
import com.example.todolist.ui.theme.ToDoListTheme

class MainActivity : ComponentActivity() {

    private val todoViewModel by viewModels<TodoViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            ToDoListTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    todoScreenActivity(todoViewModel = todoViewModel)
                }
            }
        }
    }
}

//뷰
@Composable
private fun todoScreenActivity(todoViewModel: TodoViewModel) {
    val items:List<Data> by todoViewModel.todoItem.observeAsState(listOf())
    Screen(items = items, onAddItem = {todoViewModel.addItem(it)}, onRemoveItem = {todoViewModel.removeItem(it)})
}

@Preview
@Composable
fun TodoScreenActivityPreview() {
    val viewModel = TodoViewModel() // 적절한 TodoViewModel 인스턴스를 생성합니다.
    val items = listOf( // 적절한 목록 아이템을 만듭니다.
        Data(task = "Task 1"),
        Data(task = "Task 2"),
        Data(task = "Task 3")
    )
    ToDoListTheme {
        todoScreenActivity(todoViewModel = viewModel)
    }
}