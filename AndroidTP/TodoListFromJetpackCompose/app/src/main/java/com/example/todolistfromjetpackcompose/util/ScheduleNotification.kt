package com.example.todolistfromjetpackcompose.util

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
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    // 알림을 트리거할 시간 설정 (date와 time을 Calendar 객체로 변환)
    val calendar = Calendar.getInstance().apply {
        val (year, month, day) = date.split("-").map { it.toInt() }
        val (hour, minute) = time.split(":").map { it.toInt() }
        set(Calendar.YEAR, year)
        set(Calendar.MONTH, month - 1) // Calendar.MONTH는 0부터 시작함
        set(Calendar.DAY_OF_MONTH, day)
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, minute)
        set(Calendar.SECOND, 0)
    }

    // PendingIntent 설정
    val intent = Intent(context, NotificationReceiver::class.java).apply {
        putExtra("notificationId", alarmId)
        putExtra("message", plan)
    }

    val pendingIntent = PendingIntent.getBroadcast(
        context,
        alarmId,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    // 알람 예약
    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            Log.e("AlarmError", "정확한 알람 설정 권한이 필요합니다.")
        }

        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )
        Toast.makeText(context, "알람이 설정되었습니다.", Toast.LENGTH_SHORT).show()
    } catch (e: SecurityException) {
        Log.e("AlarmError", "알람 설정 실패: ${e.message}")
    }
}
