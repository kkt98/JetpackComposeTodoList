package com.kkt98.todolistfromjetpackcompose.repository

import com.kkt98.todolistfromjetpackcompose.room.PlanDao
import com.kkt98.todolistfromjetpackcompose.room.PlanEntity
import javax.inject.Inject

class PlanRepository @Inject constructor(private val planDao: PlanDao) {

    //일정 추가
    suspend fun insertSchedule(planEntity: PlanEntity) = planDao.insertPlan(planEntity)
    fun getSchedulesByDate(date: String) = planDao.getPlansByDate(date)

    // 일정 세부 정보 수정
    suspend fun updatePlan(planEntity: PlanEntity) {
        planDao.updatePlan(planEntity)
    }

    // 일정 삭제
    suspend fun deletePlan(planEntity: PlanEntity) {
        planDao.deletePlan(planEntity)
    }

    // 모든 일정을 가져오는 함수 추가
    fun getAllSchedules() = planDao.getAllPlans()

}