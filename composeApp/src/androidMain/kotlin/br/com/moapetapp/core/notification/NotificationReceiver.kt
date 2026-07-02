package br.com.moapetapp.core.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import br.com.moapetapp.R

/**
 * Recebe o disparo do AlarmManager no horário agendado e exibe a notificação.
 * Registrado no AndroidManifest
 */
class NotificationReceiver : BroadcastReceiver() {
    companion object {
        // Chaves do Intent - usadas pelo scheluder pra passar os dados da notificação
        const val EXTRA_ID = "extra_id"
        const val EXTRA_TITLE = "extra_title"
        const val EXTRA_BODY = "extra_body"

        // Canal genérico, reaproveitado por vacina/alimentação/medicação
        const val CHANNEL_ID = "reminders"
        const val CHANNEL_NAME = "Lembretes"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val id = intent.getStringExtra(EXTRA_ID) ?: return
        val title = intent.getStringExtra(EXTRA_TITLE)?: ""
        val body = intent.getStringExtra(EXTRA_BODY)?: ""

        // Garante que o canal existe
        ensureChannel(context)

        // Monta a notificação
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        // Exibe. O id(String UUID) vira Int estável para a API de notificação do Android
        val notificationId = id.hashCode()
        // Checagem defensiva: se a permissão foi revogada, notify lançaria SecurityException
        if (NotificationManagerCompat.from(context).areNotificationsEnabled()) {
            NotificationManagerCompat.from(context).notify(notificationId, notification)
        }
    }

    // Cria o canal de notificação
    private fun ensureChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH,
            ).apply { 
                description = "Lembretes de vacinas, medicações e alimentação"
            }
            val manager = context.getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }
}
