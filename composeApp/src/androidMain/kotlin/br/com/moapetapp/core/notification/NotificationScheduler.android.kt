package br.com.moapetapp.core.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import kotlinx.datetime.Instant
import org.koin.mp.KoinPlatform.getKoin
import androidx.core.app.NotificationManagerCompat

actual fun provideNotificationScheduler(): NotificationScheduler =
    AndroidNotificationScheduler(getKoin().get<Context>())

private class AndroidNotificationScheduler(
    private val context: Context,
) : NotificationScheduler {

    override suspend fun requestPermission(): Boolean {
        return NotificationManagerCompat.from(context).areNotificationsEnabled()
    }

    override suspend fun scheduleNotification(
        id: String,
        title: String,
        body: String,
        triggerAt: Instant
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        
        val intent = Intent(context, NotificationReceiver::class.java).apply {
            putExtra(NotificationReceiver.EXTRA_ID, id)
            putExtra(NotificationReceiver.EXTRA_TITLE, title)
            putExtra(NotificationReceiver.EXTRA_BODY, body)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Agenda o alarme para o instante exato
        alarmManager.setAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerAt.toEpochMilliseconds(),
            pendingIntent
        )
    }

    override fun cancelNotification(id: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            id.hashCode(),
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
        }
    }
}
