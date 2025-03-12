package com.kkt98.todolistfromjetpackcompose.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.kkt98.todolistfromjetpackcompose.screens.CalenderScreen
import com.kkt98.todolistfromjetpackcompose.screens.PlanListScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun BottomNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = BottomNavItem.Calender.route
    ) {
        composable(route = BottomNavItem.Calender.route){
            CalenderScreen()
        }
        composable(route = BottomNavItem.List.route){
            PlanListScreen()
        }
    }
}