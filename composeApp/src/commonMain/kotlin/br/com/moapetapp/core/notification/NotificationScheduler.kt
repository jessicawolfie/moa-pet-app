package br.com.moapetapp.core.notification

import kotlinx.datetime.Instant

/**
 * Agenda e cancela notificações locais. Cpntrato comum;
 * cada plataforma forneceo actual
 */
interface NotificationScheduler {

    /**
     * Pede permissão de notificação ao usuário.
     * suspend porque o usuário responde a um diálogo do sistema - é assíncrono.
     * @return true se concedida; false se negada
     */
    suspend fun requestPermission(): Boolean

    /**
     * Agenda uma notificação local para disparar em [triggerAt].
     * @param id identificador único (use o id da vacina) - permite cancelar/sobrescrever depois.
     * @param title título da notificação.
     * @param body corpo da notificação.
     * @param triggerAt instante (UTC) em que deve disparar.
     */
    suspend fun scheduleNotification(
        id: String,
        title: String,
        body: String,
        triggerAt: Instant
    )

    /**
     * Cancela uma notificação agendada.
     * No-op se não houver nada agendado com esse id.
     */
    fun cancelNotification(id: String)
}

// Fábrica fornecida por cada plataforma
expect fun provideNotificationScheduler(): NotificationScheduler