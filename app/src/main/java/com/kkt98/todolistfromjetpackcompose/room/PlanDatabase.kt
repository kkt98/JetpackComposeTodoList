package com.kkt98.todolistfromjetpackcompose.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [PlanEntity::class], version = 3)
abstract class PlanDatabase: RoomDatabase() {

    abstract fun planDao(): PlanDao

}