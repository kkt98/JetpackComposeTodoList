package com.example.todolistfromjetpackcompose.repository

import com.example.todolistfromjetpackcompose.room.PlanDao
import com.example.todolistfromjetpackcompose.room.PlanEntity
import javax.inject.Inject

class PlanRepository @Inject constructor(private val planDao: PlanDao) {

    //일정 추가
    suspend fun insertSchedule(planEntity: PlanEntity) = planDao.insertPlan(planEntity)
    fun getSchedulesByDate(date: String) = planDao.getPlansByDate(date)

    // 일정 세부 정보 수정
    suspend fun updatePlan(planEntity: PlanEntity) {
        planDao.updatePlan(planEntity)
    }

    // ID를 사용하여 세부 정보 수정
    suspend fun updateDetail(id: Int, detail: String) {
        planDao.updateDetail(id, detail)
    }

    // 일정 삭제
    suspend fun deletePlan(planEntity: PlanEntity) {
        planDao.deletePlan(planEntity)
    }

    // ID를 사용하여 일정 삭제
    suspend fun deletePlanById(id: Int) {
        planDao.deletePlanById(id)
    }

    // 모든 일정을 가져오는 함수 추가
    fun getAllSchedules() = planDao.getAllPlans()

}