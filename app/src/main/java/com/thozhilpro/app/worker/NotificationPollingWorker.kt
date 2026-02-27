package com.thozhilpro.app.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.thozhilpro.app.MainActivity
import com.thozhilpro.app.R
import com.thozhilpro.app.data.api.ApiService
import com.thozhilpro.app.data.local.PreferencesManager
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class NotificationPollingWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val apiService: ApiService,
    private val preferencesManager: PreferencesManager
) : CoroutineWorker(context, workerParams) {

    companion object {
        const val WORK_NAME = "notification_polling"
        private const val CHANNEL_ID = "thozhilpro_polling"
        private const val CHANNEL_NAME = "ThozhilPro Alerts"
    }

    override suspend fun doWork(): Result {
        return try {
            val token = preferencesManager.getToken()
            if (token.isNullOrEmpty()) return Result.success()

            val res = apiService.getUnreadCount()
            if (res.isSuccessful) {
                val count = res.body()?.get("count") ?: 0
                if (count > 0) {
                    showNotification("ThozhilPro", "You have $count unread notification${if (count > 1) "s" else ""}")
                }
            }
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    private fun showNotification(title: String, body: String) {
        val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
            manager.createNotificationChannel(channel)
        }

        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pi = PendingIntent.getActivity(applicationContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setContentIntent(pi)
            .build()

        manager.notify(1001, notification)
    }
}
