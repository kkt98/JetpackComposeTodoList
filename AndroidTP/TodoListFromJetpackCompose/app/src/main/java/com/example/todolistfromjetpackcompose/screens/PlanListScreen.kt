package com.example.todolistfromjetpackcompose.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PlanListScreen() {

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            stickyHeader {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.LightGray
                ) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        text = "치킨",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )
                }
            }
            items(5) { index ->
                EmptyContent("치킨 $index")
            }

            stickyHeader {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.LightGray
                ) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        text = "피자",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )
                }
            }
            items(5) { index ->
                EmptyContent("피자 $index")
            }

            stickyHeader {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.LightGray
                ) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        text = "짜장면",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )
                }
            }
            items(20) { index ->
                EmptyContent("짜장면 $index")
            }
        }

}


@Composable
private fun EmptyContent(
    contentNum: String
){
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF8A8484))
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 30.dp),
            text = contentNum,
            textAlign = TextAlign.Center,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
}

