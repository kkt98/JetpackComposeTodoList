package com.example.todolistfromjetpackcompose.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todolistfromjetpackcompose.repository.PlanRepository
import com.example.todolistfromjetpackcompose.room.PlanEntity
import com.example.todolistfromjetpackcompose.util.scheduleNotification
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CalenderPlanViewModel @Inject constructor(
    private val planRepository: PlanRepository // 데이터베이스와 상호작용하는 리포지토리 주입
) : ViewModel() {

    // 저장, 수정, 삭제 상태 관리
    private val _operationStatus = MutableStateFlow<String?>(null)
    val operationStatus: StateFlow<String?> = _operationStatus

    // 선택된 날짜에 해당하는 일정 목록을 저장
    private val _schedules = MutableStateFlow<List<PlanEntity>>(emptyList())
    val schedules: StateFlow<List<PlanEntity>> = _schedules

    // 저장된 날짜 목록을 저장하는 StateFlow
    private val _savedDates = MutableStateFlow<List<String>>(emptyList())
    val savedDates: StateFlow<List<String>> = _savedDates

    // 주어진 날짜에 해당하는 일정 목록을 로드하는 함수
    fun getSchedulesByDate(date: String) {
        viewModelScope.launch {
            // 리포지토리를 통해 일정 목록을 불러오고 StateFlow로 전달
            planRepository.getSchedulesByDate(date).collect { plans ->
                _schedules.value = plans
            }
        }
    }

    // 일정 추가 기능
    fun insertSchedule(context: Context, date: String, plan: String, time: String, alarm: Boolean) {
        viewModelScope.launch {
            val planEntity = PlanEntity(date = date, plan = plan, time = time, alarm = alarm)
            planRepository.insertSchedule(planEntity)
            getSchedulesByDate(planEntity.date)
            _operationStatus.value = "저장 완료" // 저장 완료 상태 설정

            if (alarm) {
                // 알람 설정이 체크된 경우 알람 예약
                val alarmId = planEntity.hashCode() // 고유한 알람 ID를 생성 (중복 방지를 위해)
                scheduleNotification(context, date, time, alarmId, planEntity.plan )
            }
        }
    }

    // 일정 삭제 기능
    fun deleteSchedule(planEntity: PlanEntity, plan: String) {
        viewModelScope.launch {
            planRepository.deletePlan(planEntity)
            when (plan) {
                "selectPlan" -> getSchedulesByDate(planEntity.date)
                else -> getAllSchedules()
            }
            _operationStatus.value = "삭제 완료" // 삭제 완료 상태 설정
        }
    }

    // 일정 수정 기능
    fun updateSchedule(planEntity: PlanEntity, plan: String) {
        viewModelScope.launch {
            planRepository.updatePlan(planEntity)
            when (plan) {
                "selectPlan" -> getSchedulesByDate(planEntity.date)
                else -> getAllSchedules()
            }
            _operationStatus.value = "수정 완료" // 수정 완료 상태 설정
        }
    }

    // 모든 일정을 조회하고, 일정이 있는 날짜만 저장
    fun loadSavedDates() {
        viewModelScope.launch {
            planRepository.getAllSchedules().collect { plans ->
                _savedDates.value = plans.map { it.date }.distinct()
            }
        }
    }

    // 모든 일정을 가져오는 함수
    fun getAllSchedules() {
        viewModelScope.launch {
            planRepository.getAllSchedules().collect { plans ->
                _schedules.value = plans  // 모든 일정을 StateFlow로 전달
            }
        }
    }

    // 완료 상태 초기화
    fun resetOperationStatus() {
        _operationStatus.value = null
    }
}