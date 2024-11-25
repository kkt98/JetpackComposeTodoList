package com.example.todolistfromjetpackcompose.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PlanDao {
    @Insert
    suspend fun insertPlan(plan: PlanEntity)

    @Query("SELECT * FROM planentity WHERE date = :date")
    fun getPlansByDate(date: String): Flow<List<PlanEntity>>

    // 일정 세부 정보 수정
    @Update
    suspend fun updatePlan(plan: PlanEntity)

    // ID를 사용하여 세부 정보 수정
    @Query("UPDATE planentity SET `plan` = :detail WHERE id = :id")
    suspend fun updateDetail(id: Int, detail: String)

    // 일정 삭제
    @Delete
    suspend fun deletePlan(plan: PlanEntity)

    // ID를 사용하여 일정 삭제
    @Query("DELETE FROM planentity WHERE id = :id")
    suspend fun deletePlanById(id: Int)

    @Query("SELECT * FROM planentity")  // 모든 일정을 가져오는 SQL 쿼리
    fun getAllPlans(): Flow<List<PlanEntity>>  // 모든 일정 반환
}