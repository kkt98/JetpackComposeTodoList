package com.kkt98.todolistfromjetpackcompose.util

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import java.util.Calendar

@SuppressLint("ScheduleExactAlarm")
fun scheduleNotification(context: Context, date: String, time: String, alarmId: Int, plan: String) {
    // 알람 매니저를 통해 알림을 예약할 수 있도록 설정
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    // 알림을 트리거할 정확한 시간을 설정하기 위해 Calendar 객체로 변환
    val calendar = Calendar.getInstance().apply {
        // 전달받은 date와 time을 년, 월, 일, 시간, 분으로 분해하여 설정
        val (year, month, day) = date.split("-").map { it.toInt() }
        val (hour, minute) = time.split(":").map { it.toInt() }
        set(Calendar.YEAR, year)
        set(Calendar.MONTH, month - 1)  // Calendar.MONTH는 0부터 시작함에 유의
        set(Calendar.DAY_OF_MONTH, day)
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, minute)
        set(Calendar.SECOND, 0)
    }

    // NotificationReceiver에 전달할 PendingIntent를 생성
    val intent = Intent(context, NotificationReceiver::class.java).apply {
        putExtra("notificationId", alarmId)  // 알림의 고유 ID 전달
        putExtra("message", plan)  // 알림에 표시할 메시지로 일정 계획 내용 전달
    }

    // PendingIntent 생성 및 설정 (기존의 Intent가 있을 경우 업데이트)
    val pendingIntent = PendingIntent.getBroadcast(
        context,
        alarmId,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    // 알람 예약 설정
    try {
        // Android S 이상에서는 정확한 알람 권한이 필요함
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            Log.e("AlarmError", "정확한 알람 설정 권한이 필요합니다.")
        }

        // 정확한 시간에 알람이 울리도록 설정
        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )
        Toast.makeText(context, "알람이 설정되었습니다.", Toast.LENGTH_SHORT).show()
    } catch (e: SecurityException) {
        // 권한이 없거나 오류 발생 시 예외 처리
        Log.e("AlarmError", "알람 설정 실패: ${e.message}")
    }
}
