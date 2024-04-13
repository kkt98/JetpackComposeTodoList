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
    private val planRepository: PlanRepository
) : ViewModel() {

    private val _saveSuccess = MutableStateFlow(false)
    val saveSuccess: StateFlow<Boolean> = _saveSuccess

    // 선택된 날짜에 대한 일정 로드
    private val _schedules = MutableStateFlow<List<PlanEntity>>(emptyList())
    val schedules: StateFlow<List<PlanEntity>> = _schedules

    fun insertSchedule(date: String, plan: String) {
        viewModelScope.launch {
            val planEntity = PlanEntity(date = date, plan = plan)
            planRepository.insertSchedule(planEntity)
            _saveSuccess.value = true
            _saveSuccess.value = false
        }
    }

    fun getSchedulesByDate(date: String) {
        viewModelScope.launch {
            planRepository.getSchedulesByDate(date).collect { plans ->
                _schedules.value = plans
            }
        }
    }

    fun deleteSchedule(planEntity: PlanEntity) {
        viewModelScope.launch {
            planRepository.deletePlan(planEntity)
            // 일정 삭제 후, 해당 날짜의 일정을 다시 로드하여 UI를 업데이트 합니다.
            getSchedulesByDate(planEntity.date)
            // 삭제 성공 알림 (예: 상태 플래그 변경)
            _saveSuccess.value = true
            _saveSuccess.value = false
        }
    }

}