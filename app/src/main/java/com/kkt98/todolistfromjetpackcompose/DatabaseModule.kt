package com.kkt98.todolistfromjetpackcompose

import android.content.Context
import androidx.room.Room
import com.kkt98.todolistfromjetpackcompose.repository.PlanRepository
import com.kkt98.todolistfromjetpackcompose.room.PlanDao
import com.kkt98.todolistfromjetpackcompose.room.PlanDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun providePlanDatabase(@ApplicationContext appContext: Context): PlanDatabase {
        return Room.databaseBuilder(appContext, PlanDatabase::class.java, "plan_database")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Singleton
    @Provides
    fun providePlanDao(database: PlanDatabase): PlanDao {
        return database.planDao()
    }

    @Singleton
    @Provides
    fun providePlanRepository(
        planDao: PlanDao
    ) : PlanRepository {
        return PlanRepository(planDao)
    }
}