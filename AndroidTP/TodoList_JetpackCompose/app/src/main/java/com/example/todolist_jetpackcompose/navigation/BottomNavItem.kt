package com.example.todolist_jetpackcompose.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.List
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: String,
    val title: String, // 표시될 이름
    val icon: ImageVector // 표시될 아이콘
){
    data object Calender: BottomNavItem(
        route = "calender",
        title = "Calender",
        icon =  Icons.Default.DateRange
    )
    data object List: BottomNavItem(
        route = "list",
        title = "List",
        icon =  Icons.Default.List
    )
}