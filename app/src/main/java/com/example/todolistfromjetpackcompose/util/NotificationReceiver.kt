package com.example.todolistfromjetpackcompose.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.todolistfromjetpackcompose.MainActivity
import com.example.todolistfromjetpackcompose.R

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // Intent에서 알림 ID와 메시지를 추출
        val notificationId = intent.getIntExtra("notificationId", 0)
        val message = intent.getStringExtra("message") ?: "You have a task scheduled!"  // 메시지가 없을 경우 기본 메시지 설정

        // 알림 매니저 생성
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // 알림 채널 ID와 이름 설정 (안드로이드 Oreo 이상에서 필요)
        val channelId = "schedule_alarm_channel"
        val channelName = "Schedule Alarm"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // 알림 채널 생성 (중요도 설정 포함)
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }

        // 알림 클릭 시 열릴 액티비티 설정 (여기서는 MainActivity로 설정)
        val contentIntent = PendingIntent.getActivity(
            context, 0, Intent(context, MainActivity::class.java), PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // 알림을 생성하는 NotificationCompat.Builder 설정
        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_background)  // 알림 아이콘 설정
            .setContentTitle("알림")  // 알림 제목 설정
            .setContentText(message)  // 알림 내용 설정
            .setContentIntent(contentIntent)  // 알림 클릭 시 contentIntent 실행
            .setAutoCancel(true)  // 알림 클릭 시 자동으로 삭제
            .build()

        // 알림을 표시
        notificationManager.notify(notificationId, notification)
    }
}
