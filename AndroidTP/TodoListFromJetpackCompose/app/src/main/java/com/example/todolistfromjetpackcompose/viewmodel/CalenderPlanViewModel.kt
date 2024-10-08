package com.example.todolistfromjetpackcompose.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todolistfromjetpackcompose.repository.PlanRepository
import com.example.todolistfromjetpackcompose.room.PlanEntity
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
    fun insertSchedule(date: String, plan: String) {
        viewModelScope.launch {
            val planEntity = PlanEntity(date = date, plan = plan)
            planRepository.insertSchedule(planEntity)
            getSchedulesByDate(planEntity.date)
            _operationStatus.value = "저장 완료" // 저장 완료 상태 설정
        }
    }

    // 일정 삭제 기능
    fun deleteSchedule(planEntity: PlanEntity) {
        viewModelScope.launch {
            planRepository.deletePlan(planEntity)
            getSchedulesByDate(planEntity.date)
            _operationStatus.value = "삭제 완료" // 삭제 완료 상태 설정
        }
    }

    // 일정 수정 기능
    fun updateSchedule(planEntity: PlanEntity) {
        viewModelScope.launch {
            planRepository.updatePlan(planEntity)
            getSchedulesByDate(planEntity.date)
            _operationStatus.value = "수정 완료" // 수정 완료 상태 설정
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