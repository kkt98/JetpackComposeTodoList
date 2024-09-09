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

    // 일정 저장 또는 삭제 성공 여부를 UI에 전달하는 플래그
    private val _saveSuccess = MutableStateFlow(false)
    val saveSuccess: StateFlow<Boolean> = _saveSuccess

    // 선택된 날짜에 해당하는 일정 목록을 저장
    private val _schedules = MutableStateFlow<List<PlanEntity>>(emptyList())
    val schedules: StateFlow<List<PlanEntity>> = _schedules

    // 새로운 일정을 추가하는 함수
    fun insertSchedule(date: String, plan: String) {
        viewModelScope.launch {
            // 새로운 일정 엔티티 생성
            val planEntity = PlanEntity(date = date, plan = plan)
            // 리포지토리를 통해 일정 저장
            planRepository.insertSchedule(planEntity)
            getSchedulesByDate(planEntity.date)
            // 저장 성공 후 saveSuccess 플래그를 true로 설정
            _saveSuccess.value = true
        }
    }

    // 주어진 날짜에 해당하는 일정 목록을 로드하는 함수
    fun getSchedulesByDate(date: String) {
        viewModelScope.launch {
            // 리포지토리를 통해 일정 목록을 불러오고 StateFlow로 전달
            planRepository.getSchedulesByDate(date).collect { plans ->
                _schedules.value = plans
            }
        }
    }

    // 주어진 일정을 삭제하는 함수
    fun deleteSchedule(planEntity: PlanEntity) {
        viewModelScope.launch {
            // 리포지토리를 통해 일정 삭제
            planRepository.deletePlan(planEntity)
            // 삭제 후 해당 날짜에 맞는 일정 목록을 다시 로드하여 UI 업데이트
            getSchedulesByDate(planEntity.date)
            // 삭제 성공 후 saveSuccess 플래그를 true로 설정한 뒤 초기화
            _saveSuccess.value = true
            _saveSuccess.value = false
        }
    }

    // 일정 수정 기능 추가
    fun updateSchedule(planEntity: PlanEntity) {
        viewModelScope.launch {
            // PlanRepository의 updatePlan을 호출하여 일정 수정
            planRepository.updatePlan(planEntity)
            // 수정 후 해당 날짜의 일정을 다시 불러와서 UI 업데이트
            getSchedulesByDate(planEntity.date)
        }
    }

    // 일정의 세부 정보를 ID로 수정하는 함수
    fun updateScheduleDetail(id: Int, detail: String) {
        viewModelScope.launch {
            // PlanRepository의 updateDetail을 호출하여 세부 정보 수정
            planRepository.updateDetail(id, detail)
        }
    }

    // saveSuccess 상태를 초기화하는 함수
    fun resetSaveSuccess() {
        _saveSuccess.value = false
    }
}
