package com.example.todolistfromjetpackcompose.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.todolistfromjetpackcompose.screens.CalenderScreen
import com.example.todolistfromjetpackcompose.screens.PlanListScreen

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