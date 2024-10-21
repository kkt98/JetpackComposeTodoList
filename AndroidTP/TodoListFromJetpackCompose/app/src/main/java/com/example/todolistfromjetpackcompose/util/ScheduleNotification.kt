package com.example.todolistfromjetpackcompose.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.util.Calendar

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
    alarmManager.setExact(
        AlarmManager.RTC_WAKEUP,
        calendar.timeInMillis,
        pendingIntent
    )
}
