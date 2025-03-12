package com.kkt98.todolistfromjetpackcompose.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PlanEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val date: String,
    val plan: String,
    val time: String,
    val alarm: Boolean,
)