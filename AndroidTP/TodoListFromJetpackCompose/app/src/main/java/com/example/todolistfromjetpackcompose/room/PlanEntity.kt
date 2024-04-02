package com.example.todolistfromjetpackcompose.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PlanEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val date: String,
    val plan: String
)